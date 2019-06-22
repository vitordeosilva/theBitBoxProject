import time
import pigpio
import RPi.GPIO as GPIO
import requests
import json


class MotorController:

    def __init__(self, stepPins, directionPins, servoPins, servoFactors, servoPwm):
        self.pi = pigpio.pi()
        
        self.rampPosition2x = [[400,84],[500,105],[625,132],[1000,211],[1250,264],[1250,1200],[1250,132],[1000,105],[625,66],[500,52],[400,49]]
        
        self.rampPosition2y = [[400,84],[500,105],[625,132],[1000,211],[1250,264],[1250,1200],[1250,132],[1000,105],[625,66],[500,52],[400,49]]
        
        self.rampPosition1x = [[400,28],[500,35],[625,44],[1000,70],[1250,88],[1250,400],[1250,44],[1000,35],[625,22],[500,17],[400,17]]
        
        self.rampPosition1y = [[400,42],[500,52],[625,66],[1000,105],[1250,132],[1250,600],[1250,66],[1000,52],[625,33],[500,26],[400,26]]

        self.servoFactors = servoFactors
        self.stepperPins = stepPins
        self.servoPins = servoPins
        self.servoPwm = servoPwm
        self.directionPins = directionPins
        
        #stepPins = [stepPin1, stepPin2]
        #servoPins = [servo1, servo2, servo3, servo4]

    def moveSteppers(self, ramp1, ramp2):
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
        self.pi.set_PWM_dutycycle(self.servoPins[servoNumber],0)
        self.pi.set_PWM_frequency(self.servoPins[servoNumber],frequency)
        print("Movimentando servo: " + str(servoNumber+1))
        self.pi.set_PWM_dutycycle(self.servoPins[servoNumber], self.servoPwm[servoNumber])
        time.sleep(0.8*self.servoFactors[servoNumber])
        self.pi.set_PWM_dutycycle(self.servoPins[servoNumber],0)
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

        for i in range(len(directionPins)):
            self.pi.write(directionPins[i], False)
        self.moveSteppers(rampX, rampY)

        self.dispenseProduct(servoNumber)


        for i in range(len(directionPins)):
            self.pi.write(directionPins[i], True)

        self.moveSteppers(rampX, rampY)        
        
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


    

    
    
stepPin1 = 20
directionPin1 = 21

stepPin2 = 6
directionPin2 = 13

servo1 = 25
servo2 = 8
servo3 = 7
servo4 = 18




getUrl = 'https://aqueous-peak-23356.herokuapp.com/dispensar/1'
baseUrl = 'https://aqueous-peak-23356.herokuapp.com/transacoes/1'

completedTransitionCode = 1
ongoingTransitionCode = 2
newTransitionCode = 3

stepperPins= [stepPin1, stepPin2]
servoPins = [servo1, servo2, servo3, servo4]
servoFactors = [1,1,1,1]
servoPwm = [11,12,12,12]
directionPins = [directionPin1, directionPin2]

controller = MotorController(stepperPins, directionPins, servoPins, servoFactors, servoPwm)


controller.pi.set_mode(servo1, pigpio.OUTPUT)
controller.pi.set_mode(servo2, pigpio.OUTPUT)
controller.pi.set_mode(servo3, pigpio.OUTPUT)
controller.pi.set_mode(servo4, pigpio.OUTPUT)

#rJson = requests.get(getUrl).json()

try:
    controller.driveMotors(2,2)
except KeyboardInterrupt:
	print("\n Interrupt pressed, clearing pins")
finally:
    controller.exit()