import subprocess, json

config = None
config_file = "C:/WebDiff/config.json"

if __name__ == "__main__":
  cFile = open(config_file, "r")
  config = json.loads(cFile.read())
  cFile.close()
  bMap = config["vm_config"]["browser_map"]
  for b in bMap:
	subprocess.Popen([bMap[b][1]])