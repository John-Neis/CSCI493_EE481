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

Servo myservo; int target_pos_ms = 0; int current_pos_ms = 1000; int step_pos_ms = 0; int delay_ms = 0;
int ble_override = 0;
//short sampleBuffer[256];

//volatile int samplesRead;

void setup() {
  // Start Serial
  myservo.attach(D6);
  myservo.writeMicroseconds(current_pos_ms);
  
  Serial.begin(9600);

  // Ensure Serial port is ready
  while(!Serial);

  //Prepare LED
  pinMode(LED_BUILTIN, OUTPUT);
  pinMode(LED_PIN, OUTPUT);
  pinMode(A0, OUTPUT);
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
   
  BLEDevice central = BLE.central();

  if(central)
  {
    // Only send data if connected to central device
    while(central.connected())
    {
      connectedLight();

      if(ble_override)
      {
        move_finger(target_pos_ms, step_pos_ms, delay_ms);
      }
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
  byte msg_buffer[256];
  
  char cmd_byte[256];
  char finger[1];
  char target[256];
  char stepsize[256];
  char delaylength[256];
  
  char* args[5] = {cmd_byte, finger, target, stepsize, delaylength};
  
  int dataLength = rxChar.readValue(msg_buffer, 256);
  int arg_counter = 0;
  int arg_index = 0;
  
  for(int i = 0; i < dataLength; i++)
  {
    if(msg_buffer[i] == ' ') {
      args[arg_counter][arg_index] = '\0';
      
      arg_counter++;
      arg_index = 0;
      continue;
    }
    if(arg_counter == 5) {
      break;
    }
    args[arg_counter][arg_index++] = msg_buffer[i];
  }
  int cmd = atoi(cmd_byte); int target_pos = atoi(target);
  int stepping = atoi(stepsize); int delayms = atoi(delaylength);

  if(cmd == CMD_OVERRIDE) {
    ble_override = 1;
    target_pos_ms = target_pos;
    step_pos_ms = stepping;
    delay_ms = delayms;
  }
  Serial.print("Recieved: ") Serial.println(msg_buffer);
  Serial.print("\nCommand byte: "); Serial.println(cmd);
  Serial.print("Target byte: "); Serial.println(target_pos);
  Serial.print("Step size: "); Serial.println(stepping);
  Serial.print("Delay (MS): "); Serial.println(delayms);
  Serial.print("Value length: "); Serial.println(rxChar.valueLength());
}

void move_finger(int target, int step_ms, int time_delay) {

  if(target < current_pos_ms) {
    Serial.println("step is negative");
    step_ms *= -1; // If target is less than current, need to step backwards.
  }
  
  while(current_pos_ms != target) {
    if(current_pos_ms < target && current_pos_ms + step_ms > target) {
      current_pos_ms = target;
      myservo.writeMicroseconds(current_pos_ms);
      Serial.print("Servo written: "); Serial.println(current_pos_ms);
      delay(time_delay);
      break;
    }

    if(current_pos_ms > target && current_pos_ms + step_ms < target) {
      current_pos_ms = target;
      myservo.writeMicroseconds(current_pos_ms);
      Serial.print("Servo written: "); Serial.println(current_pos_ms);
      delay(time_delay);
      break;
    }
    
    current_pos_ms += step_ms;
    myservo.writeMicroseconds(current_pos_ms);
    Serial.print("Servo written: "); Serial.println(current_pos_ms);
    delay(time_delay);
  }
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
