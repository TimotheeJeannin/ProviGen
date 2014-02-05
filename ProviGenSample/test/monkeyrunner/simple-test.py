# Imports the monkeyrunner modules used by this program
import sys, os

from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice

def ensure_dir(f):
    d = os.path.dirname(f)
    if not os.path.exists(d):
        os.makedirs(d)

def screenshot(n, deviceName, device):
    # Takes a screenshot
    result = device.takeSnapshot()
    # Writes the screenshot to a file
    screenshotName = 'target/monkeyrunner/'+deviceName+'/monkeyrunner-shot%d.png' % (n,)
    ensure_dir(screenshotName)
    result.writeToFile(screenshotName,'png')

def main():
  # Connects to the current device, returning a MonkeyDevice object
  timeOut = 10;

  if len(sys.argv) >= 2:
    deviceName = sys.argv[1]
    device = MonkeyRunner.waitForConnection(timeOut, deviceName)
  else:
    deviceName = 'defaultDevice'
    device = MonkeyRunner.waitForConnection(timeOut)

  # Installs the Android package. Notice that this method returns a boolean, so you can test
  # to see if the installation worked.
  device.installPackage('target/ProviGen-sample-1.6.0-SNAPSHOT.apk')
  
  # sets a variable with the package's internal name
  package = 'com.tjeannin.provigen.sample'

  # sets a variable with the name of an Activity in the package
  activity = 'com.tjeannin.provigen.sample.MainActivity'

  # sets the name of the component to start
  runComponent = package + '/' + activity

  # Runs the component
  device.startActivity(component=runComponent)

  MonkeyRunner.sleep(2)
  screenshot(1, deviceName, device)

  device.press('DPAD_RIGHT', MonkeyDevice.DOWN_AND_UP)
  device.press('DPAD_CENTER', MonkeyDevice.DOWN_AND_UP)

  MonkeyRunner.sleep(2)
  screenshot(2, deviceName, device)
  
  device.press('DPAD_LEFT', MonkeyDevice.DOWN_AND_UP)
  device.press('DPAD_CENTER', MonkeyDevice.DOWN_AND_UP)
  device.press('DPAD_CENTER', MonkeyDevice.DOWN_AND_UP)
  
  MonkeyRunner.sleep(2)
  screenshot(3, deviceName, device)

  device.press('DPAD_RIGHT', MonkeyDevice.DOWN_AND_UP)
  device.press('DPAD_CENTER', MonkeyDevice.DOWN_AND_UP)
  
  MonkeyRunner.sleep(2)
  screenshot(4, deviceName, device)

  device.press('DPAD_LEFT', MonkeyDevice.DOWN_AND_UP)
  device.press('DPAD_CENTER', MonkeyDevice.DOWN_AND_UP)
  
  MonkeyRunner.sleep(2)
  screenshot(5, deviceName, device)
  
  device.removePackage(package)
  
main()
