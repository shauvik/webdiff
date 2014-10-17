import json

f = open("google_dom.txt", "r")
data = json.loads(f.read())
f.close()

ids = []
nodes = {}
wl = [data]

while len(wl) != 0:
	d = wl.pop()
	id = int(d['id'])
	ids.append(id)
	nodes[id] = d
	for c in d['children']:
		wl.append(c)

ids.sort()
for id in ids:
    #Print nodeid, nodename
	#print id, nodes[id]['name']
    #Print nodeid, parent, children
	if('parentId' in nodes[id]['data']):
		print id, nodes[id]['data']['parentId'], nodes[id]['data']['childElements']
	else:
		print id
	#for d in nodes[id]['data']:
	#	print d
