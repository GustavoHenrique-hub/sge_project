package com.enterprise.converter;

import com.enterprise.model.entity.gestao.AlunoEntity;
import com.enterprise.service.gestao.AlunoService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("alunoEntityConverter")
@RequestScoped
public class AlunoEntityConverter implements Converter<AlunoEntity> {

    @Inject
    private AlunoService alunoService;

    @Override
    public AlunoEntity getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            Long id = Long.valueOf(value);
            return alunoService.findById(id).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, AlunoEntity value) {
        if (value == null || value.getId() == null) {
            return "";
        }
        return String.valueOf(value.getId());
    }
}