"""Finish intermediary member remapping before decompilation.

Intermediary member IDs are globally stable. This also restores inherited
overrides and record accessor names that a remapper without source-namespace
Minecraft inheritance information cannot resolve.
"""
import collections,json,pathlib,re,struct,sys,zipfile
candidates=[]
for p in pathlib.Path.home().joinpath('.gradle/caches/fabric-loom').rglob('*.tiny'):
    try:
        h=p.open().readline().strip().split('\t')
        if h[:3]==['tiny','2','0'] and 'intermediary' in h and 'named' in h:
            candidates.append((p,h))
    except (OSError,UnicodeError):
        pass
mapping={}
for p,h in candidates:
    source=3+h[3:].index('intermediary')
    target=3+h[3:].index('named')
    for line in p.read_text().splitlines():
        c=line.split('\t')
        if len(c)>max(source,target) and c[0]=='' and c[1] in ('m','f'):
            a,b=c[source],c[target]
            if re.fullmatch(r'(method|field|comp)_\d+',a) and a!=b:
                mapping.setdefault(a,collections.Counter())[b]+=1
if not mapping:
    raise SystemExit('Missing intermediary -> Mojang member mappings')
resolved={k:v.most_common(1)[0][0] for k,v in mapping.items()}
ref=pathlib.Path('.port-reference');ref.mkdir(exist_ok=True)
(ref/'member-map.json').write_text(json.dumps(resolved,indent=2,sort_keys=True))
(ref/'ambiguous-members.json').write_text(json.dumps({k:dict(v) for k,v in mapping.items() if len(v)>1},indent=2,sort_keys=True))
pattern=re.compile(rb'\b(?:method|field|comp)_\d+\b')
def rewrite(data):
    count=struct.unpack_from('>H',data,8)[0]
    result=bytearray(data[:10]);pos=10;i=1
    while i<count:
        tag=data[pos];pos+=1;result.append(tag)
        if tag==1:
            size=struct.unpack_from('>H',data,pos)[0];pos+=2
            value=data[pos:pos+size];pos+=size
            value=pattern.sub(lambda m:resolved.get(m[0].decode(),m[0].decode()).encode(),value)
            result+=struct.pack('>H',len(value))+value
        else:
            size={3:4,4:4,5:8,6:8,7:2,8:2,9:4,10:4,11:4,12:4,15:3,16:2,17:4,18:4,19:2,20:2}[tag]
            result+=data[pos:pos+size];pos+=size
            if tag in (5,6):i+=1
        i+=1
    result+=data[pos:]
    return result
jar=pathlib.Path(sys.argv[1]);temp=jar.with_suffix('.fixed.jar')
with zipfile.ZipFile(jar) as src,zipfile.ZipFile(temp,'w',zipfile.ZIP_DEFLATED) as dst:
    for entry in src.infolist():
        data=src.read(entry.filename)
        if entry.filename.endswith('.class'):data=rewrite(data)
        dst.writestr(entry,data)
temp.replace(jar)
print('Restored',len(resolved),'Minecraft member names before decompiling.')
