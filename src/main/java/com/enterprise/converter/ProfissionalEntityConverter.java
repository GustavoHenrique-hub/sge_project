package com.enterprise.converter;

import com.enterprise.model.entity.gestao.AlunoEntity;
import com.enterprise.model.entity.gestao.ProfissionalEntity;
import com.enterprise.service.gestao.AlunoService;
import com.enterprise.service.gestao.ProfissionalService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("profissionalEntityConverter")
@RequestScoped
public class ProfissionalEntityConverter implements Converter<ProfissionalEntity> {

    @Inject
    private ProfissionalService profissionalService;

    @Override
    public ProfissionalEntity getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            Long id = Long.valueOf(value);
            return profissionalService.findById(id).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, ProfissionalEntity value) {
        if (value == null || value.getId() == null) {
            return "";
        }
        return String.valueOf(value.getId());
    }
}
