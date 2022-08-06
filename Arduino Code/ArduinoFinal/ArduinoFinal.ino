#include <SoftwareSerial.h>
#include <BuckPSU.h>

String command;
int voltageCommand;
SoftwareSerial mySerial(10,11);
BuckPSU psu(mySerial);

void setup() {
  Serial.begin(9600);
  mySerial.begin(9600);
}

void loop() {
  if(Serial.available()){
        command = Serial.readStringUntil('\n');
        if (command == "off") {
          psu.enableOutput(false);
        }
        if (command == "on") {
          psu.enableOutput(true);
        }
        if (command == "v") {
          voltage();
        }
    } 
}

void voltage() {
  while (Serial.available() == 0) {
  }
  voltageCommand = Serial.parseInt();
  psu.setVoltageMilliVolts(voltageCommand);
}
