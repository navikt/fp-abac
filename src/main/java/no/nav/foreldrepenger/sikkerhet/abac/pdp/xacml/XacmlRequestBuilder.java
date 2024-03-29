package no.nav.foreldrepenger.sikkerhet.abac.pdp.xacml;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class XacmlRequestBuilder {

    private static final String REQUEST = "Request";

    private final Map<Category, List<XacmlAttributeSet>> attributeSets = new EnumMap<>(Category.class);

    public XacmlRequestBuilder addResourceAttributeSet(XacmlAttributeSet attributeSet) {
        addAttributeSetInCategory(Category.Resource, attributeSet);
        return this;
    }

    public XacmlRequestBuilder addEnvironmentAttributeSet(XacmlAttributeSet attributeSet) {
        addAttributeSetInCategory(Category.Environment, attributeSet);
        return this;
    }

    public XacmlRequestBuilder addActionAttributeSet(XacmlAttributeSet attributeSet) {
        addAttributeSetInCategory(Category.Action, attributeSet);
        return this;
    }

    public XacmlRequestBuilder addSubjectAttributeSet(XacmlAttributeSet attributeSet) {
        addAttributeSetInCategory(Category.AccessSubject, attributeSet);
        return this;
    }

    private void addAttributeSetInCategory(Category category, XacmlAttributeSet decisionPoint) {

        if (attributeSets.containsKey(category)) {
            attributeSets.get(category).add(decisionPoint);
        } else {
            List<XacmlAttributeSet> setList = new ArrayList<>();
            setList.add(decisionPoint);
            attributeSets.put(category, setList);
        }
    }

    public JsonObject build() {
        JsonObjectBuilder categories = Json.createObjectBuilder();

        Set<Category> keys = attributeSets.keySet();
        for (Category xacmlCategory : keys) {
            List<XacmlAttributeSet> attrsList = attributeSets.get(xacmlCategory);
            if (attrsList.size() == 1) {
                categories.add(xacmlCategory.name(), attrsList.get(0).Attributes());
            } else {
                JsonArrayBuilder bob = Json.createArrayBuilder();
                for (XacmlAttributeSet attributeSet : attrsList) {
                    bob.add(attributeSet.Attributes());
                }
                categories.add(xacmlCategory.name(), bob);
            }
        }

        attributeSets.clear();
        return Json.createObjectBuilder().add(REQUEST, categories).build();
    }
}
