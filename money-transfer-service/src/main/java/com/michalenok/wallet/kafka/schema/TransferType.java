/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.michalenok.wallet.kafka.schema;
@org.apache.avro.specific.AvroGenerated
public enum TransferType implements org.apache.avro.generic.GenericEnumSymbol<TransferType> {
  CREDIT, DEBIT, INTERNAL  ;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"enum\",\"name\":\"TransferType\",\"namespace\":\"com.michalenok.wallet.kafka.schema\",\"symbols\":[\"CREDIT\",\"DEBIT\",\"INTERNAL\"]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
}