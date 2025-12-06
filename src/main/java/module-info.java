module ais {
  requires javafx.controls;
  requires javafx.graphics;
  requires org.hibernate.orm.core;
  requires jakarta.persistence;
  requires org.xerial.sqlitejdbc;
  requires org.slf4j;

  opens ais.model to org.hibernate.orm.core;
  opens ais to javafx.graphics;

  exports ais;
}
