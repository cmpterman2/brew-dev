package org.blh.beerxml.parser;

import java.io.File;
import java.util.List;
import org.blh.beerxml.ClassToRecordNameMapper.NoRecordNameException;
import org.blh.beerxml.ParseException;
import org.blh.beerxml.type.BeerXMLRecord;
import org.blh.beerxml.type.BeerXMLRecordSet;
import java.io.InputStream;

/**
 * Represents a class that can parse a BeerXML version 1 file.
 *
 * @author thinner
 */
public interface BeerXMLParser {

    List<BeerXMLRecordSet<BeerXMLRecord>> parse(InputStream xmlFile) throws ParseException, NoRecordNameException;
}
