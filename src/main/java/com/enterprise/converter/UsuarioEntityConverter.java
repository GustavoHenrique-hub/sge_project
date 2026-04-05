package com.enterprise.converter;

import com.enterprise.model.entity.admin.UsuarioEntity;
import com.enterprise.service.admin.UsuarioService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.inject.Inject;
import jakarta.inject.Named;
/**
 * Converter JSF usado para transformar valores da interface em objetos de UsuarioEntityConverter e vice-versa.
 */

@Named("usuarioEntityConverter")
@RequestScoped
public class UsuarioEntityConverter implements Converter<UsuarioEntity> {

    @Inject
    private UsuarioService usuarioService;
    /**
     * Converte o valor textual enviado pela interface para o objeto correspondente usado no backend.
     */

    @Override
    public UsuarioEntity getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            Long id = Long.valueOf(value);
            return usuarioService.findById(id).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    /**
     * Converte o objeto selecionado em texto para que o componente JSF consiga renderizar o valor.
     */

    @Override
    public String getAsString(FacesContext context, UIComponent component, UsuarioEntity value) {
        if (value == null || value.getId() == null) {
            return "";
        }
        return String.valueOf(value.getId());
    }
}