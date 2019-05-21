import os
import signal
import RPi.GPIO as GPIO
import time


def terminate(signalNumber, frame):
    GPIO.cleanup()
    exit(0)


def wait():
    try:
        time.sleep(1)
    except (InterruptedError, KeyboardInterrupt):
        GPIO.cleanup()


signal.signal(signal.SIGUSR1, terminate)

GPIO.setmode(GPIO.BOARD)
GPIO.setup(8, GPIO.IN, pull_up_down=GPIO.PUD_UP)

while GPIO.input(8) == GPIO.HIGH:
    wait()
os.system('wall "encore un aller-retour du switch pour démarrer"')
os.system("sudo killall -9 java")
while GPIO.input(8) == GPIO.LOW:
    wait()
os.system('wall "basculer le switch pour démarrer"')
while GPIO.input(8) == GPIO.HIGH:
    wait()
GPIO.cleanup()
os.system('wall "démarrage"')
os.system("sudo killall -9 java")
os.system("/home/pi/TechTheTachyon-HighLevel/run_master_from_python.sh")

"""
while True:
    while GPIO.input(8) == GPIO.LOW:
        wait()
    os.system("sudo killall -9 java")
    os.system('wall "HL tué"')
    while GPIO.input(8) == GPIO.HIGH:
        wait()
    os.system("/home/pi/TechTheTachyon-HighLevel/run_master_from_python.sh")
    os.system('wall "HL relancé"')
"""
