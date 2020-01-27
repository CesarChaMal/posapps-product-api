CREATE TABLE Category (
  id int(11) NOT NULL AUTO_INCREMENT,
  created datetime DEFAULT NULL,
  updated datetime DEFAULT NULL,
  version int(11) DEFAULT NULL,
  name varchar(255) DEFAULT NULL,
  parent_id int(11) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE Device (
  id int(11) NOT NULL AUTO_INCREMENT,
  created datetime DEFAULT NULL,
  event varchar(255) DEFAULT NULL,
  name varchar(255) DEFAULT NULL,
  updated datetime DEFAULT NULL,
  version int(11) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE Product (
  id int(11) NOT NULL AUTO_INCREMENT,
  created datetime NOT NULL,
  updated datetime DEFAULT NULL,
  version int(11) NOT NULL,
  name varchar(255) DEFAULT NULL,
  description varchar(255) DEFAULT NULL,
  sku varchar(255) DEFAULT NULL,
  barcode varchar(255) DEFAULT NULL,
  barcodeType varchar(255) DEFAULT NULL,
  imagePath varchar(255) DEFAULT NULL,
  sellingPrice float (10,2) DEFAULT NULL,
  systemUserId bigint(20) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE Product_Device (
  Product_id int(11) NOT NULL,
  devices_id int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE Product_Category (
  Product_id int(11) NOT NULL,
  categories_id int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
