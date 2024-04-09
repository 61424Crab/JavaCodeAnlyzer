import os
import sys
sys.stdout = console

filePathSrc="E:\\new\\" # Path to the folder with files to convert

for root, dirs, files in os.walk(filePathSrc):
    for fn in files:
        print fn[-5:]
        if fn[-5:]=='.json':# Specify type of the files
            print fn[-5:]
            notepad.open(root + "\\" + fn)
            notepad.runMenuCommand("Encoding","Convert to UTF-8")
            notepad.save()
            notepad.close()
