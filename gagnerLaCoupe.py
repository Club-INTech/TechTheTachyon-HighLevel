import os
import signal
import RPi.GPIO as GPIO
import time


def terminate(signalNumber, frame):
    GPIO.cleanup()
    restoreMotd()
    exit(0)


def wait():
    try:
        time.sleep(0.2)
    except (InterruptedError, KeyboardInterrupt):
        GPIO.cleanup()
        restoreMotd()


def restoreMotd():
    os.system('cp -f /etc/motd_save /etc/motd')


def writeMotd(message):
    os.system('sudo echo -e "\n\e[34m[HL] ' + message + '\n\e[0m" >>/etc/motd')
    os.system('sudo wall -n "[HL] ' + message + '"')


# catch 'killall -10 python3'
signal.signal(signal.SIGUSR1, terminate)
signal.signal(signal.SIGTERM, terminate)

# save motd
# os.system('sudo cp -f /etc/motd /etc/motd_save')

GPIO.setmode(GPIO.BOARD)
GPIO.setup(8, GPIO.IN, pull_up_down=GPIO.PUD_UP)

if GPIO.input(8) == GPIO.LOW:
    writeMotd("Positionner l\'interrupteur sur 0 pour commencer")
while GPIO.input(8) == GPIO.LOW:
    wait()


while True:
    restoreMotd()
    writeMotd("Positionner l\'interrupteur sur 1 pour lancer le HL")
    while GPIO.input(8) == GPIO.HIGH:
        wait()
    os.system("sudo /home/pi/TechTheTachyon-HighLevel/run_master_from_python.sh &")
    restoreMotd()
    writeMotd("HL lancé!")

    restoreMotd()
    writeMotd("Positionner l\'interrupteur sur 0 pour stopeer le HL")
    while GPIO.input(8) == GPIO.LOW:
        wait()
    os.system("sudo killall -9 java")
    PID = open("/home/pi/panneauRaspi/LED/PID", "r")
    killPID = PID.readline()
    PID.close()
    os.system("sudo kill -10 " + killPID)
    restoreMotd()
    writeMotd("HL stoppé")

GPIO.cleanup()