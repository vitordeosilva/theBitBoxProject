import time
import pigpio
import RPi.GPIO as GPIO
import requests
import json

import requests
import json


class Usuario:
    id = -1  # Long
    nome = ''  # Str
    senha = ''  # Str
    idCarteira = ''  # Str

    def __init__(self, id, nome, senha, idCarteira):
        self.id = id
        self.nome = nome
        self.senha = senha
        self.idCarteira = idCarteira

    def toJson(self):
        return json.dumps(self, default=lambda o: o.__dict__, sort_keys=True, indent=4)


class Maquina:
    id = -1
    idCarteira = ''

    def __init__(self, id, idCarteira):
        self.id = id
        self.idCarteira = idCarteira

    def toJson(self):
        return json.dumps(self, default=lambda o: o.__dict__, sort_keys=True, indent=4)


class Produto:
    id = -1
    nome = ""
    preco = -1.0

    def __init__(self, id, nome, preco):
        self.id = id
        self.nome = nome
        self.precoUnitario = preco

    def toJson(self):
        return json.dumps(self, default=lambda o: o.__dict__, sort_keys=True, indent=4)


class Trilha:
    id = -1
    maquinaID = -1
    produtoID = -1
    qtdeProdutos = -1
    posicaoLinha = -1
    posicaoColuna = -1

    def __init__(self, id, maquinaID, produtoID, qtdeProdutos, posicaoLinha, posicaoColuna):
        self.id = id
        self.maquinaID = maquinaID
        self.produtoID = produtoID
        self.qtdeProdutos = qtdeProdutos
        self.posicaoLinha = posicaoLinha
        self.posicaoColuna = posicaoColuna

    def toJson(self):
        return json.dumps(self, default=lambda o: o.__dict__, sort_keys=True, indent=4)


class Transacao:
    id = -1
    estado = -1
    usuarioID = -1
    maquinaID = -1
    produtoID = -1

    def __init__(self, id, estado, usuarioID, maquinaID, produtoID):
        self.id = id
        self.estado = estado
        self.usuarioID = usuarioID
        self.maquinaID = maquinaID
        self.produtoID = produtoID

    def toJson(self):
        return json.dumps(self, default=lambda o: o.__dict__, sort_keys=True, indent=4)


url_base = 'http://aqueous-peak-23356.herokuapp.com'


def addUser(usuario):
    return requests.post(url_base + '/usuarios', json=usuario.__dict__)


def addMaquina(maquina):
    return requests.post(url_base + '/maquinas', json=maquina.__dict__)


def addProduto(produto):
    return requests.post(url_base + '/produtos', json=produto.__dict__)


def addTrilha(trilha):
    return requests.post(url_base + '/trilhas', json=trilha.__dict__)


def addTransacao(transacao):
    return requests.post(url_base + '/transacoes', json=transacao.__dict__)


def mudaEstadoTransacao(id_trans, estado):
    return requests.post(url_base + '/transacoes/' + str(id_trans), data={'estado': estado})



class MotorController:

    def __init__(self, stepPins, directionPins, servoPins):
        self.pi = pigpio.pi()
        
        self.rampPosition2x = [[400,84],[500,105],[625,132],[1000,211],[1250,264],[1250,1200],[1250,132],[1000,105],[625,66],[500,52],[400,49]]
        
        self.rampPosition2y = [[400,84],[500,105],[625,132],[1000,211],[1250,264],[1250,1200],[1250,132],[1000,105],[625,66],[500,52],[400,49]]
        
        self.rampPosition1x = [[400,28],[500,35],[625,44],[1000,70],[1250,88],[1250,400],[1250,44],[1000,35],[625,22],[500,17],[400,17]]
        
        self.rampPosition1y = [[400,42],[500,52],[625,66],[1000,105],[1250,132],[1250,600],[1250,66],[1000,52],[625,33],[500,26],[400,26]]

        self.stepperPins = stepPins
        self.servoPins = servoPins
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
        pwm = GPIO.PWM(pin, 200)
        pwm.start(90)
        time.sleep(0.8)
        pwm.stop()
        time.sleep(0.8)

    def driveMotors(self, positionX, positionY):
        if(positionX == 1 and positionY == 1):  
            print("Movimentando servo 1")
            servoNumber = 0
            rampX = self.rampPosition1x
            rampY = self.rampPosition1y
            print("Movimentando para a posicao (1,1)")
        elif(positionX == 2 and positionY == 1):
            print("Movimentando servo 2")
            servoNumber = 1
            rampX = self.rampPosition2x
            rampY = self.rampPosition1y
            print("Movimentando para a posicao (2,1)")
        elif(positionX == 1 and positionY == 2):
            print("Movimentando servo 3")
            servoNumber = 2
            rampX = self.rampPosition1x
            rampY = self.rampPosition2y
            print("Movimentando para a posicao (1,2)")
        else:
            print("Movimentando servo 4")
            servoNumber = 3
            rampX = self.rampPosition2x
            rampY = self.rampPosition2y
            print("Movimentando para a posicao (2,2)")

        for i in range(len(directionPins)):
            self.pi.write(directionPins[i], True)
        self.moveSteppers(rampX, rampY)

        self.dispenseProduct(servoNumber)


        for i in range(len(directionPins)):
            self.pi.write(directionPins[i], False)

        self.moveSteppers(rampX, rampY)        
        
    def setup(self):
        GPIO.setmode(GPIO.BCM)
        for i in range(len(servoPins)):
            GPIO.setup(servoPins[i], GPIO.OUTPUT)

    def exit(self):
        GPIO.cleanup()
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


    
    
stepPin1 = 20
directionPin1 = 21

stepPin2 = 6
directionPin2 = 13

servo1 = 25
servo2 = 8
servo3 = 7
servo4 = 18



#pi.set_mode(servo1, pigpio.OUTPUT)
#pi.set_mode(servo2, pigpio.OUTPUT)
#pi.set_mode(servo3, pigpio.OUTPUT)
#pi.set_mode(servo4, pigpio.OUTPUT)

GPIO.setmode(GPIO.BCM)
GPIO.setup(servo1, GPIO.OUT)
GPIO.setup(servo2, GPIO.OUT)
GPIO.setup(servo3, GPIO.OUT)
GPIO.setup(servo4, GPIO.OUT)

getUrl = 'https://aqueous-peak-23356.herokuapp.com/dispensar/1'
baseUrl = 'https://aqueous-peak-23356.herokuapp.com/transacoes/1'

completedTransitionCode = 1
ongoingTransitionCode = 2
newTransitionCode = 3

stepperPins= [stepPin1, stepPin2]
servoPins = [servo1, servo2, servo3, servo4]
directionPins = [directionPin1, directionPin2]

controller = MotorController(stepperPins, directionPins, servoPins)

rJson = requests.get(getUrl).json()

try:
    print("Esperando transacao: ")
    
    while(rJson == -1):
        rJson = requests.get(getUrl).json()
        time.sleep(0.5)
        
    print("Transacao iniciada, processando" + str(rJson))
    #mudaEstadoTransacao(10, ongoingTransitionCode)        
        
    positionX = rJson[0]
    positionY = rJson[1]

    controller.driveMotors(positionX, positionY)
                
    #controller.driveMotors(1,1)

    #controller.driveMotors(2,1)

    #controller.driveMotors(1,2) 

    #controller.driveMotors(2,2)  
    
    #mudaEstadoTransacao(10, completedTransitionCode)
            	        
    print("\n Processamento completo, retornando")

    time.sleep(1)

    #params = {'estado': completedTransactionCode}
    #requests.post(url, params)

            
except KeyboardInterrupt:
	print("\n Interrupt pressed, clearing pins")
finally:
    controller.exit()
