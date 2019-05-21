import os
import signal
import RPi.GPIO as GPIO
import time


def terminate(signalNumber, frame):
    GPIO.cleanup()
    exit(0)


if __name__ == '__main__':
    signal.signal(signal.SIGTERM, terminate)

    GPIO.setmode(GPIO.BOARD)

    GPIO.setup(8, GPIO.IN, pull_up_down=GPIO.PUD_UP)

    while GPIO.input(8) == GPIO.HIGH:
        time.sleep(1)
    os.system('wall "encore un aller-retour du switch pour démarrer"')
    os.system("sudo killall -9 java")
    while GPIO.input(8) == GPIO.LOW:
        time.sleep(1)
    os.system('wall "basculer le switch pour démarrer"')
    while GPIO.input(8) == GPIO.HIGH:
        time.sleep(1)
    GPIO.cleanup()
    os.system('wall "démarrage"')
    os.system("sudo killall -9 java")
    os.system("/home/pi/TechTheTachyon-HighLevel/run_master_from_python")
