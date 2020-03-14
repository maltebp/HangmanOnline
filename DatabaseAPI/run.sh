mvn clean package\
&& scp -r -i "$AMAZON_KEY" target/DatabaseAPI-*.jar ec2-user@maltebp.dk:./HangmanOnline\
&& ssh -i "$AMAZON_KEY" ec2-user@maltebp.dk "cd /home/ec2-user/HangmanOnline && ./dbapi_start.sh"
