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

        self.QRCode=Image.open("/home/pi/Projects/theBitBoxProject/VendingMachine/QRCode.png")
        #creating square canvas in the middle of the screen
        self.canvasH = self.screenH
        self.canvasW = self.canvasH
        self.positionX = 0
        self.positionY = 0
        self.canvas = tk.Canvas(self.root, width=self.canvasW, height=self.canvasH, bd = 0,highlightthickness=0)
        self.canvas.configure(background='white')
        self.canvas.pack()
        #load up image sequence of GIF
        #gifPaths = ["LoadingGifs/gif{0}.gif".format(i) for i in range(1, 5)]
        gifPaths = '/home/pi/Projects/theBitBoxProject/VendingMachine/LoadingGifs/copper.gif'
        imgWidth, imgHeight = Image.open(gifPaths).size
        ratio = min(self.canvasW / imgWidth, self.canvasH / imgHeight)
        imgWidth = int(imgWidth*ratio)
        imgHeight = int(imgHeight*ratio)
        print(imgWidth, imgHeight)
        self.imageSequence = [ImageTk.PhotoImage(img.resize((imgWidth,imgHeight), Image.ANTIALIAS))
                            for img in ImageSequence.Iterator(Image.open(gifPaths))]
        self.canvas.image = self.QRCode
        self.animationCounter = 0
        self.gif_label = Label(self.canvas, bd = 0,highlightthickness=0, background = 'white')
        self.gif_timer_id = -1
    def showQRCode(self):
        imgWidth, imgHeight = self.QRCode.size
        self.animationCounter = 0
        ratio = min(self.canvasW / imgWidth, self.canvasH / imgHeight)
        imgWidth = int(imgWidth*ratio)
        imgHeight = int(imgHeight*ratio)
        pilImage = self.QRCode.resize((self.canvasW,self.canvasW), Image.ANTIALIAS)
        self.photo = ImageTk.PhotoImage(pilImage)
        imagesprite = self.canvas.create_image(0,0,anchor = NW, image=self.photo)
        self.canvas.configure(background='black')
        self.canvas.image = self.QRCode
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

#thread que faz qualquer coisa em paralelo
class ThExemplo(Thread):
    carregando = False
    def __init__ (self):
        Thread.__init__(self)
    def run(self):
        try:
            i = 0
            while i < 2:
                self.carregando=False
                time.sleep(2)
                self.carregando=True
                print ("Carregando")
                time.sleep(10)
                self.carregando=False
                print ("Pronto")
                i = i + 1
        except KeyboardInterrupt:
                print("Acabou a festa")
root = tk.Tk()
UTK = UtilTK(root)

th = ThExemplo()
th.start()
####



mostrandoImagem = False

try:
    while True:
        UTK.iterate()
        UTK.updateGIF()
        if th.carregando == False and mostrandoImagem == False:
            UTK.gif_label.pack_forget()
            UTK.showQRCode()
            mostrandoImagem = True
        elif th.carregando == True and mostrandoImagem == True:
            if UTK.gif_timer_id != -1:
                root.after_cancel(UTK.gif_timer_id)
            UTK.gif_label.pack()
            mostrandoImagem = False
except KeyboardInterrupt:
    root.destroy()
