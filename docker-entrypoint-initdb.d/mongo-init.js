print('Start #################################################################');

db = db.getSiblingDB('producto-service-prod');
db.createUser(
  {
    user: 'root',
    pwd: 'root',
    roles: [{ role: 'readWrite', db: 'producto-service-prod' }],
  },
);
db.createCollection('users');

db = db.getSiblingDB('producto-service-dev');
db.createUser(
  {
    user: 'dev',
    pwd: 'dev',
    roles: [{ role: 'readWrite', db: 'producto-service-dev' }],
  },
);
db.createCollection('users');

db = db.getSiblingDB('producto-service-test');
db.createUser(
  {
    user: 'test',
    pwd: 'test',
    roles: [{ role: 'readWrite', db: 'producto-service-test' }],
  },
);
db.createCollection('users');

print('END #################################################################');