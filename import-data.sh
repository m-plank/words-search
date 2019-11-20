echo 'Creating mongodb indexes...'
mongo wikipedia --eval 'db.usages.createIndex({body:"text"})'
mongo wikipedia --eval 'db.usages.createIndex( { "id": 1 }, { unique: true } )'

echo 'Importing data to mongodb...'
mongoimport --db=wikipedia --collection=usages --type=json  --file=./src/it/resources/usages.json

