#!/usr/bin/env python3
import tkinter as tk
from tkinter import *
import time
from PIL import Image, ImageTk
    
    
    
root = tk.Tk()
# get screen width and height
ws = root.winfo_screenwidth() # width of the screen
hs = root.winfo_screenheight() # height of the screen
    
# calculate x and y coordinates for the Tk root window
x = (ws/2) - (hs/2)
y = (hs/2) - (hs/2)

# set the dimensions of the screen 
# and where it is placed
root.geometry('%dx%d+%d+%d' % (hs, hs, x, y))
root.attributes("-fullscreen", True)
root.configure(background='black')

class Example(Frame):
    def __init__(self, master, *pargs):
        Frame.__init__(self, master, *pargs)
        self.image = Image.open("/home/pi/Projects/theBitBoxProject/VendingMachine/RiQRoll.png")
        self.img_copy= self.image.copy()
        self.background_image = ImageTk.PhotoImage(self.image)
        self.background = Label(self, image=self.background_image)
        self.background.pack(fill=BOTH, expand=YES)
        self.background.bind('<Configure>', self._resize_image)

    def _resize_image(self,event):
        new_width = event.width
        new_height = event.height
        self.image = self.img_copy.resize((new_width, new_height))
        self.background_image = ImageTk.PhotoImage(self.image)
        self.background.configure(image =  self.background_image)



e = Example(root)
e.pack(fill=BOTH, expand=YES)
i = 0
try:
    while i < 1000:
        root.update_idletasks()
        root.update()
        i = i + 1
except KeyboardInterrupt:
    root.destroy()