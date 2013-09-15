#include <Servo.h>
Servo servo1;
           //Aruidno Driver   Motor   Android
int FW1 = 2;//  2   -  15   -   14        1
int BW1 = 3;//  3   -  10   -   11        2
int FW2 = 5;//  5   -  2    -   3         3
int BW2 = 4;//  4   -  7    -   6         4

void setup()
{
  int i;
  for(i=2;i<=5;i++){
    pinMode(i, OUTPUT); 
    analogWrite(i,0);
    //digitalWrite(i,LOW);
  }
  servo1.attach(9);//定义舵机控制口
  Serial.begin(115200);//设置波特率
}
void loop()
{
  int dataLen = 0;    //数据长度
  int start_flag = 0; //起动标志位
  int data_index=0;
  int com_index=0;    //索引初始化  
  int temp_char1;
  int temp_char0;
  int i,unm,x,y;
  int state;          //帧状态
  int SUM1,SUM2;      //效验和
  int MOTO[7]={0,0,0,0,0,0,0},RC[9];  //电机控制寄存器和舵机控制寄存器
  //RC[9]   = 55 aa 04 01 01 02 03 04 aa 
  //MOTO[7] = 55 aa 02 02 01 02  aa
  int L_ir,C_ir,R_ir; //红外线寄存器  
  int Serial_flag,Serial_unm;
  
  while(1)
  {
    int val,temp;
    temp_char1 = Serial.read();     //读取串口数据
    if(temp_char1!=-1)    //如果串口有数          
    {    
      Serial_flag=1;     //启动标志位置1
      Serial_unm=0;      //     
      if(start_flag == 0)     //等待数据头 0X55,0XAA
      {
           if( temp_char1  == 170 )  // 0XAA
           {
               if(temp_char0  == 85)  //0X55
               {
                   start_flag = 1;  //收到数据头,表示一个数据包传送开始
                   MOTO[0]=85;
                   MOTO[1]=170;
                   RC[0]=85;
                   RC[1]=170;                               
                   data_index = 0; 
                   com_index = 0;    
               }
           }else temp_char0 = temp_char1;  //170
      }else if( com_index < 2)
      { 
         switch(com_index)   
         {
             case 0 :    MOTO[2] = temp_char1;  //MOTO[2]=170
                         RC[2] = temp_char1;    //RC[2]=170
                         dataLen = temp_char1;  //dataLen=170
                         break;  
             case 1 :    MOTO[3] = temp_char1;
                         RC[3] = temp_char1;  //第2个数据,读取命令字                       
                         break;
         }
         com_index++;    //com_index=1
      }else if(data_index < dataLen && MOTO[3]==2) 
      { 
            MOTO[data_index+4] = temp_char1;
             data_index ++;
      }else if(data_index < dataLen && RC[3]==1) 
      { 
            RC[data_index+4] = temp_char1;
            data_index ++;
      }
      else
      {
            MOTO[6] = temp_char1;
            RC[8] = temp_char1;
            state=1;   // 一帧接收完毕
            start_flag = 0;
      }                  
    }
    
   /*******一帧接收完毕，开始执行*******/  
   if(state==1)
    {     
     state=0;
     if(MOTO[3]==2){
       if(MOTO[4]==1){
           analogWrite(FW1,MOTO[5]);
       }else if(MOTO[4]==2){
           analogWrite(BW1,MOTO[5]);
       }else if(MOTO[4]==3){
           analogWrite(FW2,MOTO[5]);
       }else if(MOTO[4]==4){
           analogWrite(BW2,MOTO[5]);
       }
     }
     if(RC[3]==1){

     } 
     }
   }
}
