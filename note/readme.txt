mjpg_streamer -i "input_uvc.so -d /dev/video0" -o "output_http.so -w /www/webcam -p 8080"

#ser2net
#ser2net.conf
#2001:raw:600:/dev/ttyS0:115200 NONE 1STOPBIT 8DATABITS XONXOFF LOCAL -RTSCTS
ser2net -c /etc/ser2net.conf


