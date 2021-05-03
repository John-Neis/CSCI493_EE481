// BN: Arduino Nano 33 BLE
// VID: 0x2341
// PID: 0x805a
// SN: 88F69516B1B6F281

#include <Servo.h>
#include <ArduinoBLE.h>
#define LED_PIN 13
#define WRITE_BUFFER_SIZE 256
#define WRITE_BUFFER_FIXED_LENGTH false
#define CMD_OVERRIDE 251

const char* nameOfPeripheral = "PHP Controller";
const char* uuidOfService    = "00001101-0000-1000-8000-00805f9b34fb";
const char* uuidOfRxChar     = "00001142-0000-1000-8000-00805f9b34fb";
const char* uuidOfTxChar     = "00001143-0000-1000-8000-00805f9b34fb";

BLEService handService(uuidOfService);
BLECharacteristic rxChar(uuidOfRxChar, BLEWriteWithoutResponse | BLEWrite, WRITE_BUFFER_SIZE, WRITE_BUFFER_FIXED_LENGTH);
BLEByteCharacteristic txChar(uuidOfTxChar, BLERead | BLENotify | BLEBroadcast);

Servo myservo_1; Servo myservo_2;

int target_pos_1_ms = 0; int target_pos_2_ms = 0;
int current_pos_ms_1 = 2000; int current_pos_ms_2 = 2000; int step_pos_ms = 0; 

int delay_ms = 0; int f_index = 0; int timeout = 0;
int ble_override = 0;
//short sampleBuffer[256];

//volatile int samplesRead;

void setup() {
  // Start Serial
  myservo_1.attach(D6);
  myservo_2.attach(D7);
  
  myservo_1.writeMicroseconds(2000);
  myservo_2.writeMicroseconds(2000);
  //myservo_1.write(0);
  //myservo_2.write(0);
  delay(2000);

  myservo_1.writeMicroseconds(1500);
  myservo_2.writeMicroseconds(1500);
  //myservo_1.write(90);
  //myservo_2.write(90);
  delay(2000);

  myservo_1.writeMicroseconds(1000);
  myservo_2.writeMicroseconds(1000);
  //myservo_1.write(180);
  //myservo_2.write(180);
  delay(2000);
  
  myservo_1.writeMicroseconds(2000);
  myservo_2.writeMicroseconds(2000);
  Serial.begin(9600);

  // Ensure Serial port is ready
  while(!Serial);

  //Prepare LED
  pinMode(LED_BUILTIN, OUTPUT);
  pinMode(LED_PIN, OUTPUT);
  
  pinMode(A0, INPUT);
  pinMode(A1, INPUT);
  
  //pinMode(LEDR, OUTPUT);
  //pinMode(LEDG, OUTPUT);

  // Start the BLE module
  startBLE();

  BLE.setLocalName(nameOfPeripheral);
  BLE.setAdvertisedService(handService);
  handService.addCharacteristic(rxChar);
  handService.addCharacteristic(txChar);
  BLE.addService(handService);

  BLE.setEventHandler(BLEConnected, onBLEConnect);
  BLE.setEventHandler(BLEDisconnected, onBLEDisconnect);

  rxChar.setEventHandler(BLEWritten, onRxCharValueUpdate);

  BLE.advertise();

  Serial.println("Peripheral Advertising Info: ");
  Serial.print("Name:         "); Serial.println(nameOfPeripheral);
  Serial.print("MAC:          "); Serial.println(BLE.address());
  Serial.print("Service UUID: "); Serial.println(handService.uuid());
  Serial.print("RxChar  UUID: "); Serial.println(uuidOfRxChar);
  Serial.print("TxChar  UUID: "); Serial.println(uuidOfTxChar);

  Serial.println("Bluetooth device active. Waiting for connections...");
}

void loop() {

   /*for(int wippos = 0; wippos < 180; wippos += 1) {
    //myservo.writeMicroseconds(wippos);
    myservo_1.write(wippos);
    myservo_2.write(wippos);
    delay(15);
   }
   for(int wippos = 180; wippos > 0; wippos -= 1) {
    //myservo.writeMicroseconds(wippos);
    myservo_2.write(wippos);
    myservo_1.write(wippos);
    delay(15);
   }*/

   if(timeout == 0) {
    Serial.println("Returned to analog control");
    int analogVal_1 = 0; int analogVal_2 = 0;
    analogVal_1 = analogRead(A0); analogVal_2 = analogRead(A1);
    analogVal_1 = map(analogVal_1, 0, 1023, 1000, 2000);
    analogVal_2 = map(analogVal_2, 0, 1023, 1000, 2000);

    myservo_1.writeMicroseconds(analogVal_1);
    myservo_2.writeMicroseconds(analogVal_2);   
    delay(15); 
   }
   
  BLEDevice central = BLE.central();

  if(central)
  {
    // Only send data if connected to central device
    while(central.connected())
    {
      connectedLight();

      if(timeout == 0) {
        Serial.println("Returned to analog control");
        int analogVal_1 = 0; int analogVal_2 = 0;
        analogVal_1 = analogRead(A0); analogVal_2 = analogRead(A1);
        analogVal_1 = map(analogVal_1, 0, 1023, 1000, 2000);
        analogVal_2 = map(analogVal_2, 0, 1023, 1000, 2000);

        myservo_1.writeMicroseconds(analogVal_1);
        myservo_2.writeMicroseconds(analogVal_2);   
        delay(15); 
      }

      if(ble_override)
      {
        timeout = 200000;
        move_finger(f_index, target_pos_1_ms, target_pos_2_ms, step_pos_ms, delay_ms);
      }
      Serial.println(timeout);
      if (timeout > 0) timeout--;
      
    }
  }
  else
  {
    disconnectedLight();
  }
}

/*
 * BLUETOOTH FUNCTIONS
 */

void startBLE()
{
  if(!BLE.begin())
  {
    Serial.println("Starting BLE failed!");
    while(1);
  }
}

void onBLEConnect(BLEDevice central)
{
  Serial.print("Connected event, central: "); Serial.println(central.address());

  connectedLight();
}

void onBLEDisconnect(BLEDevice central)
{
  Serial.print("Disconnected event, central: "); Serial.println(central.address());

  disconnectedLight();
}

void onRxCharValueUpdate(BLEDevice central, BLECharacteristic characteristic)
{
  //int target_pos_ms = 0; int current_pos_ms = 0; int step_pos_ms = 0; int delay_ms = 0;
  Serial.print("Characteristic event, read: ");
  char msg_buffer[256];
  
  char cmd_byte[256];
  char finger[2];
  char target_1[256];
  char target_2[256];
  char stepsize[256];
  char delaylength[256];
  
  char* args[6] = {cmd_byte, finger, target_2, target_1, stepsize, delaylength};
  
  int dataLength = rxChar.readValue(msg_buffer, 256);
  int arg_counter = 0;
  int arg_index = 0;
  
  for(int i = 0; i < dataLength; i++)
  {
    if(msg_buffer[i] == ' ') {
      args[arg_counter][arg_index] = '\0';

      if(arg_counter == 1 && args[arg_counter][0] != '2') {
        arg_counter++;
        args[arg_counter][0] = '\0';
      }
      
      arg_counter++;
      arg_index = 0;
      continue;
    }
    if(arg_counter == 6) {
      msg_buffer[i] = '\0';
      break;
    }
    args[arg_counter][arg_index++] = msg_buffer[i];
  }
  int cmd = atoi(cmd_byte); f_index = atoi(finger);
  int target_pos_1 = atoi(target_1); int target_pos_2 = 0; 
  if(f_index == 2){
    target_pos_2 = atoi(target_2);
  } else {
    target_pos_2 = -1;
  }
  int stepping = atoi(stepsize); int delayms = atoi(delaylength);

  if(cmd == CMD_OVERRIDE) {
    ble_override = 1;
    target_pos_1_ms = target_pos_1;
    target_pos_2_ms = target_pos_2;
    step_pos_ms = stepping;
    delay_ms = delayms;
  } else {
    ble_override = 0;
  }
  Serial.print("Recieved: "); Serial.println((char *)msg_buffer);
  Serial.print("\nCommand byte: "); Serial.println(cmd);
  Serial.print("Target 1 byte: "); Serial.println(target_pos_1);
  Serial.print("Target 2 byte: "); Serial.println(target_pos_2);
  Serial.print("Step size: "); Serial.println(stepping);
  Serial.print("Delay (MS): "); Serial.println(delayms);
  Serial.print("Value length: "); Serial.println(rxChar.valueLength());
}

void move_finger(int fngcount, int target_1, int target_2, int step_ms, int time_delay) {
 Servo phalanges[] = {myservo_1, myservo_2};
 int p_pos_s[] = {current_pos_ms_1, current_pos_ms_2};
 
 int both = 0;

 if(fngcount == 2) {
  both = 1;
 }

 if(!both) {
   if(target_1 < p_pos_s[fngcount]) {
     Serial.println("step is negative");
     step_ms *= -1; // If target is less than current, need to step backwards.
   }
  
   while(p_pos_s[fngcount] != target_1) {
     if(p_pos_s[fngcount] < target_1 && p_pos_s[fngcount] + step_ms > target_1) {
       p_pos_s[fngcount] = target_1;
       phalanges[fngcount].writeMicroseconds(p_pos_s[fngcount]);
       Serial.print("Servo written: "); Serial.println(p_pos_s[fngcount]);
       delay(time_delay);
       break;
     }

     if(p_pos_s[fngcount] > target_1 && p_pos_s[fngcount] + step_ms < target_1) {
       p_pos_s[fngcount] = target_1;
       phalanges[fngcount].writeMicroseconds(p_pos_s[fngcount]);
       Serial.print("Servo written: "); Serial.println(p_pos_s[fngcount]);
       delay(time_delay);
       break;
     }
    
     p_pos_s[fngcount] += step_ms;
     phalanges[fngcount].writeMicroseconds(p_pos_s[fngcount]);
     Serial.print("Servo written: "); Serial.println(p_pos_s[fngcount]);
     delay(time_delay);
   }
   current_pos_ms_1 = p_pos_s[0];
   current_pos_ms_2 = p_pos_s[1];
 } else {

 // Serial.print("Target 1: "); Serial.println(target_1);
 // Serial.print("Target 2: "); Serial.println(target_2);

 // Serial.print("Current 1: "); Serial.println(current_pos_ms_1);
 // Serial.print("Current 2: "); Serial.println(current_pos_ms_2);
  
    int step_ms_2 = step_ms;
    if(target_1 < current_pos_ms_1) {
     Serial.println("step 1 is negative");
     step_ms *= -1; // If target is less than current, need to step backwards.
   }

   if(target_2 < current_pos_ms_2) {
     Serial.println("step 2 is negative");
     step_ms_2 *= -1;
   }

  int done_1 = 0, done_2 = 0;
   while(current_pos_ms_1 != target_2 || current_pos_ms_2 != target_1) {
    //Serial.println("Loop happening");
     if(!done_1 && current_pos_ms_1 < target_2 && current_pos_ms_1 + step_ms > target_2) {
       current_pos_ms_1 = target_2;
       phalanges[0].writeMicroseconds(current_pos_ms_1);
       Serial.print("Final Servo 1 written: "); Serial.println(current_pos_ms_1);
       done_1 = 1;
       //delay(time_delay);
       //break;
     }

     if(!done_1 && current_pos_ms_1 > target_2 && current_pos_ms_1 + step_ms < target_2) {
       current_pos_ms_1 = target_2;
       phalanges[0].writeMicroseconds(current_pos_ms_1);
       Serial.print("Final Servo 1 written: "); Serial.println(current_pos_ms_1);
       done_1 = 1;
       //delay(time_delay);
       //break;
     }

     if(!done_1 && current_pos_ms_1 == target_2) {
      done_1 = 1;
      Serial.print("Final Servo 1 written: "); Serial.println(current_pos_ms_1);
     }

     if(!done_2 && current_pos_ms_2 < target_1 && current_pos_ms_2 + step_ms_2 > target_1) {
       current_pos_ms_2 = target_1;
       phalanges[1].writeMicroseconds(current_pos_ms_2);
       Serial.print("Final Servo 2 written: "); Serial.println(current_pos_ms_2);
       done_2 = 1;
       //delay(time_delay);
       //break;
     }

     if(!done_2 && current_pos_ms_2 > target_1 && current_pos_ms_2 + step_ms_2 < target_1) {
       current_pos_ms_2 = target_1;
       phalanges[1].writeMicroseconds(current_pos_ms_2);
       Serial.print("Final Servo 2 written: "); Serial.println(current_pos_ms_2);
       done_2 = 1;
       //delay(time_delay);
       //break;
     }

     if(!done_2 && current_pos_ms_2 == target_1) {
      done_2 = 1;
      Serial.print("Final Servo 2 written: "); Serial.println(current_pos_ms_2);
     }

    if(!done_1) {
       current_pos_ms_1 += step_ms;
       phalanges[0].writeMicroseconds(current_pos_ms_1);
       Serial.print("Servo 1 written: "); Serial.println(current_pos_ms_1);
    }
    if(!done_2) {
      current_pos_ms_2 += step_ms_2;
      phalanges[1].writeMicroseconds(current_pos_ms_2);
      Serial.print("Servo 2 written: "); Serial.println(current_pos_ms_2);
    }
   // Serial.print("Done 1: "); Serial.println(done_1);
   // Serial.print("Done 2: "); Serial.println(done_2);
    delay(time_delay);
   }
  // Serial.println("Loop complete");
   done_1 = 0; done_2 = 0;
   //delay(time_delay);
   //current_pos_ms_2 = current_pos_ms_1;
 }
  ble_override = 0;
}

/*
 * LED FUNCTIONS
 */

void connectedLight()
{
  digitalWrite(LED_BUILTIN, HIGH);
  digitalWrite(LED_PIN, HIGH);
  digitalWrite(A0, LOW);
}

void disconnectedLight()
{
  digitalWrite(LED_BUILTIN, LOW);
  digitalWrite(LED_PIN, LOW);
  digitalWrite(A0, HIGH);
}
