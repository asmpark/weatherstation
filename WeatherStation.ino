//Name: Alexis Park

#include "font.h"

int potenPin = 0;
int potenVal = 0;

int readIn = 0;
bool ledState = 0;
int counter = 0;

byte sendPoten(){
  Serial.write(0x21);
  Serial.write(0x30);
  Serial.write(potenVal >> 8);
  Serial.write(potenVal);
}

void readSym(){
  if(Serial.available() > 0){
    readIn = Serial.read();
    if(readIn == 0x23){
      readIn = Serial.read();
      char chaRead = (char)readIn;
       if(chaRead == 'S'){
        counter = 51;
       }
       else if(chaRead == 'P'){
        counter = 48;
       }
       else if(chaRead == 'W'){
        counter = 55;
       }
       else if(chaRead == 'F'){
        counter = 38;
       }
       else if(chaRead == 'C'){
        counter = 35;
       }
    }
  }
  
}

void setup() {
  Serial.begin(9600);
  pinMode(2, OUTPUT);   //row1
  pinMode(3, OUTPUT);   //row2
  pinMode(4, OUTPUT);   //row3
  pinMode(5, OUTPUT);   //row4
  pinMode(6, OUTPUT);   //row5
  pinMode(7, OUTPUT);   //row6
  pinMode(8, OUTPUT);   //row7
  pinMode(9, OUTPUT);   //column1
  pinMode(10, OUTPUT);  //column2
  pinMode(11, OUTPUT);  //column3
  pinMode(12, OUTPUT);  //column4
  pinMode(13, OUTPUT);  //column5
}

void loop(){
  potenVal = analogRead(potenPin);
  sendPoten();
  Serial.print("potenval: ");
  Serial.println(potenVal);
  readSym();
for(int i=0;i<5;i++){
    digitalWrite(9, HIGH);
    digitalWrite(10, HIGH);
    digitalWrite(11, HIGH);
    digitalWrite(12, HIGH);
    digitalWrite(13, HIGH);
    byte reading = font_5x7[counter][i];
    digitalWrite(i+9, LOW);  
    for(int j=7;j>0;j--){
      if(((reading>>j) & 0x01) == 0){
        ledState = LOW;
      }
      else {
        ledState = HIGH;
      }
      digitalWrite(9-j, ledState);
      
    }
    delay(3);
  
    }
  
}
