#include<Wire.h>

// AD5254 I2C address is 0x2C(44)
#define digiPot 0x2C
String serialString;
void setup(){

  Wire.begin(); // Initialises main I2C connection
  Serial.begin(9600); // Sets up serial monitor

  // Setup for Channel 1
  Wire.beginTransmission(digiPot); // Starts I2C Connection to AD5254 Digipot for first communication
  Wire.write(0x00); // Opens channel 1 from pot (Series commands)
  Wire.write(0x01); // Inputs ressistance value to 1 a.k.a. minumim
  Wire.endTransmission(); // Finishes transimission

  // Setup for Channel 2
  Wire.beginTransmission(digiPot);
  Wire.write(0x01);
  Wire.write(0x01);
  Wire.endTransmission();

  // Setup for Channel 3
  Wire.beginTransmission(digiPot);
  Wire.write(0x02);
  Wire.write(0x01);
  Wire.endTransmission();

  // Setup for Channel 4
  Wire.beginTransmission(digiPot);
  Wire.write(0x03);
  Wire.write(0x01);
  Wire.endTransmission();

  Wire.beginTransmission(digiPot);
  Wire.write(0x04);
  Wire.write(255);
  Wire.endTransmission();

  
          
  delay(300); // Delays 300ms due to errors from running too fast
}

void loop() {

  // Serial commands to use channel one ex.- (0xAa,) will input channel one with value resistance of 10 in hex

  if (Serial.available())  {
    char f = Serial.read();  //gets one byte from serial buffer
    if (f == ',') {
      if (serialString.length() >1) {
          //Serial.println(serialString); //prints string to serial port out

          String n = serialString;
          int lastIndex = serialString.length() - 1;
          n.remove(lastIndex);
        
          //Serial.println(n);
          if(serialString.indexOf('[') >0) {
           Wire.beginTransmission(digiPot);
           Wire.write(0x00);
           Wire.write(n.toInt());
           Wire.endTransmission();
           Serial.println(n);
          }
          
          if(serialString.indexOf(']') >0){
           Wire.beginTransmission(digiPot);
           Wire.write(0x01);
           Wire.write(n.toInt());
           Wire.endTransmission();
           Serial.println(n);
          }
          
          if(serialString.indexOf('{') >0) {
           Wire.beginTransmission(digiPot);
           Wire.write(0x02);
           Wire.write(n.toInt());
           Wire.endTransmission();
           Serial.println(n);
          }
          
          if(serialString.indexOf('}') >0) {
           Wire.beginTransmission(digiPot);
           Wire.write(0x03);
           Wire.write(n.toInt());
           Wire.endTransmission();
           Serial.println(n);
          }

          if(serialString.indexOf(';') >0) {
           Wire.beginTransmission(0x2F);
           Wire.write(0x00);
           Wire.write(n.toInt());
           Wire.endTransmission();
           Serial.println(n);
          }

          if(serialString.indexOf(':') >0) {
           Wire.beginTransmission(0x2F);
           Wire.write(0x01);
           Wire.write(n.toInt());
           Wire.endTransmission();
           Serial.println(n);
          }
          
          
         serialString=""; //clears variable for new input
      }
    }  
    else {     
      serialString += f; //makes the string readString
    }
  }
}
