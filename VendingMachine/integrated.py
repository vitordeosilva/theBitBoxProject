import tkinter as tk
from tkinter import *
from PIL import Image, ImageTk, ImageSequence
from threading import Thread
import time


class UtilTK():
    def __init__(self, root):
        # starting full screen
        self.root = root
        self.screenH = root.winfo_screenheight()
        self.screenW = root.winfo_screenwidth()
        self.windowH = self.screenH
        self.windowW = self.screenW
        strGeometry = str(self.windowW)+"x"+str(self.screenH)
        self.root.geometry(strGeometry)
        self.root.attributes("-fullscreen", True)
        self.root.configure(background = 'white')

        #creating square canvas in the middle of the screen
        self.canvasH = self.screenH
        self.canvasW = self.canvasH
        self.positionX = 0
        self.positionY = 0
        self.canvas = tk.Canvas(self.root, width=self.canvasW, height=self.canvasH, bd = 0,highlightthickness=0)
        self.canvas.configure(background='white')
        self.canvas.pack()
    def showImage(self, image):
        imgWidth, imgHeight = image.size
        self.animationCounter = 0
        ratio = min(self.canvasW / imgWidth, self.canvasH / imgHeight)
        imgWidth = int(imgWidth*ratio)
        imgHeight = int(imgHeight*ratio)
        pilImage = image.resize((self.canvasW,self.canvasW), Image.ANTIALIAS)
        self.photo = ImageTk.PhotoImage(pilImage)
        imagesprite = self.canvas.create_image(0,0,anchor = NW, image=self.photo)
        self.canvas.configure(background='black')
        self.canvas.image = image
        self.root.update_idletasks()
        self.root.update()
    def iterate(self):
        self.root.update_idletasks()
        self.root.update()
    def updateGIF(self): #atualiza gif
        # self.canvas.create_image(0,0,anchor = NW, image = self.imageSequence[self.animationCounter])
        # self.animationCounter = (self.animationCounter+1)%len(self.imageSequence)
        # self.root.update_idletasks()
        # self.root.update()
        frame = self.imageSequence[self.animationCounter]
        self.gif_label.configure(width = self.canvasW, height = self.canvasH, image=frame, anchor = CENTER, bd = 0,highlightthickness=0, background = 'white', highlightcolor = 'white'
                                                                                                                                                                                '')
        self.animationCounter = (self.animationCounter+1)%len(self.imageSequence)
        self.gif_timer_id = self.root.after(300000, self.updateGIF) #chama updateGIF apos 100ms

root = tk.Tk()
UTK = UtilTK(root)

#imagens
QRCode=Image.open("/home/pi/Projects/theBitBoxProject/VendingMachine/QRCode.png")
QRCode1=Image.open("/home/pi/Projects/theBitBoxProject/VendingMachine/RiQRoll.png")


mostrandoImagem = False

try:

    UTK.showImage(QRCode)

    time.sleep(3)

    UTK.showImage(QRCode1)   
    time.sleep(2)     
        
except KeyboardInterrupt:
    root.destroy()
