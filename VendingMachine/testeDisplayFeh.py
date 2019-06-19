import subprocess
from time import sleep 
#Display the image
image = subprocess.Popen(["feh", "--hide-pointer", "-x", "-q", "-B", "black", "-g", "1280x800", "teste.png"])

# Do other stuff here...
sleep(10)

# You can now close the image by doing
image.kill()
