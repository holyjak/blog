package net.jakubholy.blog.genericmappers;

import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="entry") // required by JAXB to match the bean to XML
public class MyEntryPojo {

    @Id public String myId; // the @Id annotation required by Mongo if property name isn't "_id"
    public String scalarAttribute;

}
