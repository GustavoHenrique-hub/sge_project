package com.enterprise.converter;

import com.enterprise.model.entity.admin.PerfilEntity;
import com.enterprise.service.admin.PerfilService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("perfilEntityConverter")
@RequestScoped
public class PerfilEntityConverter implements Converter<PerfilEntity> {

    @Inject
    private PerfilService perfilService;

    @Override
    public PerfilEntity getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            Long id = Long.valueOf(value);
            return perfilService.findById(id).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, PerfilEntity value) {
        if (value == null || value.getId() == null) {
            return "";
        }
        return String.valueOf(value.getId());
    }
}
