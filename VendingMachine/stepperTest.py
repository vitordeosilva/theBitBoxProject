import time
import pigpio
import RPi.GPIO as GPIO
import requests
import json

import requests
import json

import tkinter as tk
from tkinter import *
from PIL import Image, ImageTk, ImageSequence
class UtilVelocity:
    def __init__(self):
        self.velocityList10 = [400, 500, 800, 1000, 2000, 4000]
        self.velocityList4 = [400, 500, 625, 1000, 1250, 2000, 2500, 5000, 10000]
        self.velocityList8 = [200, 250, 313, 500, 625, 1000, 1250, 2500, 5000]
    def getPiChain(self, minVel, maxVel, posSlopeRatio, negSlopeRatio, stepNumber):
        if(posSlopeRatio + negSlopeRatio > 1):
            print('Invalid slope ratios')
            return
        list = self.velocityList10
        plateauRatio = 1 - posSlopeRatio - negSlopeRatio
        deltaVel = maxVel - minVel

        startingIndex = 0
        while (list[startingIndex] != minVel):
            startingIndex += 1

        finalIndex = len(list) - 1
        while (list[finalIndex] != maxVel):
            finalIndex -= 1

        sumFreq = 0

        for i in range(startingIndex, finalIndex + 1):
            sumFreq += list[i]
        print(list)
        print(stepNumber)
        print(posSlopeRatio)
        print(sumFreq)
        stepsPerTransition = posSlopeRatio * stepNumber / sumFreq

        index = 0
        chainList = []

        totalSteps = 0

        while (list[index] <= maxVel):
            deltaN = stepsPerTransition * list[index]
            chainList.append([list[index], int(deltaN)])
            # arrayString = arrayString + "["
            # arrayString = arrayString + str(list[index]) + "," + str(int(deltaN))
            # arrayString = arrayString + "], "
            totalSteps += int(deltaN)
            index += 1

        if (plateauRatio > 0):
            totalSteps += int(plateauRatio * stepNumber)
            chainList.append([list[finalIndex], int(plateauRatio * stepNumber)])
            # arrayString = arrayString + str(list[finalIndex]) + ", " + str(int(plateauRatio*numberOfSteps))
            # arrayString = arrayString + "], "

        index = finalIndex

        stepsPerTransition = stepsPerTransition * negSlopeRatio / posSlopeRatio

        while (list[index] >= minVel):
            deltaN = stepsPerTransition * list[index]
            totalSteps += int(deltaN)
            # arrayString = arrayString + "["
            if (list[index] == minVel):
                deltaN += stepNumber - totalSteps
                totalSteps += stepNumber - totalSteps
            chainList.append([list[index], int(deltaN)])
            # arrayString = arrayString + str(list[index]) + "," + str(int(deltaN))
            # arrayString = arrayString + "], "
            index -= 1
            if (index < 0):
                break
        print("Numero total de passos: " + str(totalSteps))
        return(chainList)


class MotorController:

    def __init__(self, stepPins, directionPins, servoPins, servoFactors, servoPwm):
        self.pi = pigpio.pi()

        self.UTV = UtilVelocity()

        self.rampPosition2x = [[400, 450]]

        self.rampPosition2y = self.UTV.getPiChain(400, 800, 1.0/2, 1.0/10, 1070)

        self.rampPosition1x = [[400, 1]]

        self.rampPosition1y = self.UTV.getPiChain(400, 800, 1.0/2, 1.0/10, 350)

        
        print(self.rampPosition2y)
        print(self.rampPosition2x)
        self.servoFactors = servoFactors
        self.stepperPins = stepPins
        self.servoPins = servoPins
        self.servoPwm = servoPwm
        self.directionPins = directionPins


        #stepPins = [stepPin1, stepPin2]
        #servoPins = [servo1, servo2, servo3, servo4]

    def moveSteppers1(self, ramp1, ramp2):
        l1 = len(ramp1)
        wid1 = [-1]*l1
        l2 = len(ramp2)
        wid2 = [-1]*l2

        self.pi.set_mode(stepperPins[0], pigpio.OUTPUT)
        self.pi.set_mode(stepperPins[1], pigpio.OUTPUT)

        #generating a wave for frequency for ramp1
        for i in range(l1):
            f1 = ramp1[i][0]
            micros = int(500000/f1)

            #creates waveform array for each frequency
            wf = []
            wf.append(pigpio.pulse(1<<stepperPins[0], 0       , micros))
            wf.append(pigpio.pulse(0       , 1<<stepperPins[0], micros))

            #adds wave component to waveform
            self.pi.wave_add_generic(wf)
            wid1[i] = self.pi.wave_create()

        for i in range(l2):
            f2 = ramp2[i][0]
            micros = int(500000/f2)

            #creates waveform array for each frequency
            wf = []
            wf.append(pigpio.pulse(1<<stepperPins[1], 0       , micros))
            wf.append(pigpio.pulse(0       , 1<<stepperPins[1], micros))

            #adds wave component to waveform
            self.pi.wave_add_generic(wf)
            wid2[i] = self.pi.wave_create()
        chain1 = []

        #Creates a chain for each ramp wave
        for i in range(l1):
            steps = ramp1[i][1]
            x = steps & 255
            y = steps >> 8
            chain1 += [255, 0, wid1[i],255,1,x,y]

        print(chain1)

        chain2 = []

        for i in range(l2):
            steps = ramp2[i][1]
            x = steps & 255
            y = steps >> 8
            chain2 += [255,0, wid2[i], 255,1,x,y]

        print(chain2)


        self.pi.wave_chain(chain2)
        while self.pi.wave_tx_busy():
            time.sleep(0.1)
            
        self.pi.wave_chain(chain1)
        while self.pi.wave_tx_busy():
            time.sleep(0.1)

        for i in range(l1):
            self.pi.wave_delete(wid1[i])

        for i in range(l2):
            self.pi.wave_delete(wid2[i])

    def moveSteppers2(self, ramp1, ramp2):
        l1 = len(ramp1)
        wid1 = [-1]*l1
        l2 = len(ramp2)
        wid2 = [-1]*l2

        self.pi.set_mode(stepperPins[0], pigpio.OUTPUT)
        self.pi.set_mode(stepperPins[1], pigpio.OUTPUT)

        #generating a wave for frequency for ramp1
        for i in range(l1):
            f1 = ramp1[i][0]
            micros = int(500000/f1)

            #creates waveform array for each frequency
            wf = []
            wf.append(pigpio.pulse(1<<stepperPins[0], 0       , micros))
            wf.append(pigpio.pulse(0       , 1<<stepperPins[0], micros))

            #adds wave component to waveform
            self.pi.wave_add_generic(wf)
            wid1[i] = self.pi.wave_create()

        for i in range(l2):
            f2 = ramp2[i][0]
            micros = int(500000/f2)

            #creates waveform array for each frequency
            wf = []
            wf.append(pigpio.pulse(1<<stepperPins[1], 0       , micros))
            wf.append(pigpio.pulse(0       , 1<<stepperPins[1], micros))

            #adds wave component to waveform
            self.pi.wave_add_generic(wf)
            wid2[i] = self.pi.wave_create()
        chain1 = []

        #Creates a chain for each ramp wave
        for i in range(l1):
            steps = ramp1[i][1]
            x = steps & 255
            y = steps >> 8
            chain1 += [255, 0, wid1[i],255,1,x,y]

        print(chain1)

        chain2 = []

        for i in range(l2):
            steps = ramp2[i][1]
            x = steps & 255
            y = steps >> 8
            chain2 += [255,0, wid2[i], 255,1,x,y]

        print(chain2)


        self.pi.wave_chain(chain1)
        while self.pi.wave_tx_busy():
            time.sleep(0.1)
            
        self.pi.wave_chain(chain2)
        while self.pi.wave_tx_busy():
            time.sleep(0.1)
            

        for i in range(l1):
            self.pi.wave_delete(wid1[i])

        for i in range(l2):
            self.pi.wave_delete(wid2[i])
            
    def dispenseProduct(self, servoNumber):
        pin = self.servoPins[servoNumber]
        frequency = 50
        self.pi.set_servo_pulsewidth(self.servoPins[servoNumber],0)
        self.pi.set_PWM_frequency(self.servoPins[servoNumber],frequency)
        print("Movimentando servo: " + str(servoNumber+1))
        self.pi.set_servo_pulsewidth(self.servoPins[servoNumber], self.servoPwm[servoNumber])
        time.sleep(0.8*self.servoFactors[servoNumber])
        self.pi.set_servo_pulsewidth(self.servoPins[servoNumber],0)
        time.sleep(0.8)

    def driveMotors(self, positionX, positionY):
        if(positionX == 1 and positionY == 1):
            servoNumber = 0
            rampX = self.rampPosition1x
            rampY = self.rampPosition1y
            print("Movimentando para a posicao (1,1)")
        elif(positionX == 2 and positionY == 1):
            servoNumber = 1
            rampX = self.rampPosition2x
            rampY = self.rampPosition1y
            print("Movimentando para a posicao (2,1)")
        elif(positionX == 1 and positionY == 2):
            servoNumber = 2
            rampX = self.rampPosition1x
            rampY = self.rampPosition2y
            print("Movimentando para a posicao (1,2)")
        else:
            servoNumber = 3
            rampX = self.rampPosition2x
            rampY = self.rampPosition2y
            print("Movimentando para a posicao (2,2)")

        self.pi.write(directionPins[0], True)
        self.pi.write(directionPins[1], True)

        self.moveSteppers1(rampX, rampY)
        self.dispenseProduct(servoNumber)

        self.pi.write(directionPins[0], False)
        self.pi.write(directionPins[1], False)

        self.moveSteppers2(rampX, rampY)
    def setup(self):
        GPIO.setmode(GPIO.BCM)
        for i in range(len(servoPins)):
            GPIO.setup(servoPins[i], GPIO.OUTPUT)

    def exit(self):
        GPIO.cleanup()
        for i in range(len(servoPins)):
            self.pi.write(servoPins[i], False)
        self.pi.wave_clear()
        self.pi.stop()




def generate_ramp(GPIO, ramp):
   l = len(ramp)
   wid=[-1] * l
   pi.set_mode(GPIO, pigpio.OUTPUT)

   # generate a wave per frequency

   for i in range(l):
      f = ramp[i][0]
      micros = int(500000/f)
      wf=[]
      wf.append(pigpio.pulse(1<<GPIO, 0,       micros))
      wf.append(pigpio.pulse(0,       1<<GPIO, micros))
      pi.wave_add_generic(wf)
      wid[i] = pi.wave_create()

   # generate a chain of waves

   chain = []

   for i in range(l):
      steps = ramp[i][1]
      x = steps & 255
      y = steps >> 8
      chain += [255, 0, wid[i], 255, 1, x, y]

   print(chain)

   pi.wave_chain(chain) # Transmit chain.

   while pi.wave_tx_busy(): # While transmitting.
      time.sleep(0.1)

   # delete all waves
   for i in range(l):
      pi.wave_delete(wid[i])



stepPin1 = 12
directionPin1 = 21

stepPin2 = 20
directionPin2 = 21

servo1 = 6
servo2 = 16
servo3 = 19
servo4 = 26

vdd = 5


stepperPins= [stepPin1, stepPin2]
servoPins = [servo1, servo2, servo3, servo4]
servoFactors = [1.21,1.1,1.09,1.29]
servoPwm = [900,900,900,900]
directionPins = [directionPin1, directionPin2]

controller = MotorController(stepperPins, directionPins, servoPins, servoFactors, servoPwm)


controller.pi.set_mode(vdd, pigpio.OUTPUT)
controller.pi.write(vdd, True)


controller.pi.set_mode(servo1, pigpio.OUTPUT)
controller.pi.set_mode(servo2, pigpio.OUTPUT)
controller.pi.set_mode(servo3, pigpio.OUTPUT)
controller.pi.set_mode(servo4, pigpio.OUTPUT)



try:
    while(1):
        print("Esperando transacao: ")
        
        positionX = 1
        positionY = 1
        
        controller.driveMotors(positionX, positionY)
        
        time.sleep(0.5)
                
        positionX = 2
        positionY = 1
    
        controller.driveMotors(positionX, positionY)
        print("\n Processamento completo, retornando")
        
        time.sleep(0.5)
                
        positionX = 2
        positionY = 2    
        controller.driveMotors(positionX, positionY)
        print("\n Processamento completo, retornando")

        
        time.sleep(0.5)
                
        positionX = 1
        positionY = 2    
        controller.driveMotors(positionX, positionY)
        print("\n Processamento completo, retornando")
        
        time.sleep(0.5)
    #params = {'estado': completedTransactionCode}
    #requests.post(url, params)


except KeyboardInterrupt:
	print("\n Interrupt pressed, clearing pins")
finally:
    controller.exit()
