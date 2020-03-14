# Upload entire content of src folder to website
scp -r -i "$AMAZON_KEY" src/* ec2-user@maltebp.dk:/var/www/html
