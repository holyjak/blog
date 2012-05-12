package net.jakubholy.blog.genericmappers.xml;

import javax.xml.bind.Unmarshaller.Listener;

/**
 * Processes a bean after it has bean created from XML.
 */
class BeanValidatingUnmarshalListener extends Listener {

    /**
     * Called when unmarshalling a bean is finished.
     * @param beanFromXml (required) the Java Bean the XML was mapped to
     * @param parent (optional)
     */
    @Override
    public void afterUnmarshal(Object beanFromXml, Object parent) {
        /* We would now do something like this:
         * if (beanFromXml instanceof Validatable) {
         *  ((Validatable) beanFromXml).validate();
         * }
         * where Validatable is an abstract class with validate()
         * that, by default, checks the @XmlElement annotations for
         * the 'required' attribute and consequently verifies whether the
         * property has really been set. (We could also add type-level
         * annotation like @AllPropertiesRequired.)
         */
    }

}