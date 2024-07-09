package com.orders.cabinet.model.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DrugInfo {
    String id;
    String name;
    @JsonProperty("name_ua")
    String nameUa = "";
    String form;
    @JsonProperty("form_ua")
    String formUa = "";
    String dose;
    @JsonProperty("dose_ua")
    String doseUa = "";
    String pack;
    @JsonProperty("pack_ua")
    String packUa = "";
    @JsonProperty("pack_name")
    String packName;
    @JsonProperty("pack_name_ua")
    String packNameUa;
    @JsonProperty("pack_size")
    String packSizeUa;
    @JsonProperty("pack_unit")
    String packUnit;
    @JsonProperty("pack_unit_ua")
    String packUnitUa;
    @JsonProperty("pack_note")
    String packNote;
    @JsonProperty("pack_note_ua")
    String packNoteUa = "";
    String note;
    @JsonProperty("note_ua")
    String noteUa;
    String numb = "";
    String make;
    @JsonProperty("make_ua")
    String makeUa = "";

    public String getDrugData() {
        String info = this.nameUa + " " + this.doseUa + " " + this.formUa + " " + this.packUa;
        info += " " + this.packNoteUa + ", №" + this.numb + ", " + this.makeUa;

        return info;
    }
}