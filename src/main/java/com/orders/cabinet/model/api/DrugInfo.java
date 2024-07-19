package com.orders.cabinet.model.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
/**
 * Represents information about a drug.
 *
 * <p>This class holds details about a drug, including its name, form, dosage, packaging,
 * and manufacturer information. It is used for data transfer and serialization/deserialization.</p>
 * <p> This is a response 'https://api.geoapteka.com.ua/get_item/{drugId}' </p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DrugInfo {
    /**
     * Morion identifier for the drug.
     *
     * <p>This field represents the drug's unique identifier in the database.</p>
     */
    String id;

    /**
     * Name of the drug.
     *
     * <p>This field contains the name of the drug as provided by the manufacturer.</p>
     */
    String name;

    /**
     * Name of the drug in Ukrainian.
     *
     * <p>This field contains the Ukrainian name of the drug, if available.</p>
     *
     * @default ""
     */
    @JsonProperty("name_ua")
    String nameUa = "";

    /**
     * Form of the drug.
     *
     * <p>This field describes the physical form of the drug, such as tablet, syrup, etc.</p>
     */
    String form;

    /**
     * Form of the drug in Ukrainian.
     *
     * <p>This field describes the physical form of the drug in Ukrainian, if available.</p>
     *
     * @default ""
     */
    @JsonProperty("form_ua")
    String formUa = "";

    /**
     * Dosage of the drug.
     *
     * <p>This field specifies the dosage of the drug.</p>
     */
    String dose;

    /**
     * Dosage of the drug in Ukrainian.
     *
     * <p>This field specifies the dosage of the drug in Ukrainian, if available.</p>
     *
     * @default ""
     */
    @JsonProperty("dose_ua")
    String doseUa = "";

    /**
     * Packaging details of the drug.
     *
     * <p>This field describes the packaging of the drug.</p>
     */
    String pack;

    /**
     * Packaging details of the drug in Ukrainian.
     *
     * <p>This field describes the packaging of the drug in Ukrainian, if available.</p>
     *
     * @default ""
     */
    @JsonProperty("pack_ua")
    String packUa = "";

    /**
     * Name of the drug's packaging.
     *
     * <p>This field contains the name of the packaging for the drug.</p>
     */
    @JsonProperty("pack_name")
    String packName;

    /**
     * Name of the drug's packaging in Ukrainian.
     *
     * <p>This field contains the name of the packaging for the drug in Ukrainian, if available.</p>
     */
    @JsonProperty("pack_name_ua")
    String packNameUa;

    /**
     * Size of the drug's packaging in Ukrainian.
     *
     * <p>This field specifies the size of the packaging for the drug in Ukrainian, if available.</p>
     */
    @JsonProperty("pack_size")
    String packSizeUa;

    /**
     * Unit of measure for the drug's packaging.
     *
     * <p>This field specifies the unit of measure for the drug's packaging.</p>
     */
    @JsonProperty("pack_unit")
    String packUnit;

    /**
     * Unit of measure for the drug's packaging in Ukrainian.
     *
     * <p>This field specifies the unit of measure for the drug's packaging in Ukrainian, if available.</p>
     */
    @JsonProperty("pack_unit_ua")
    String packUnitUa;

    /**
     * Additional notes about the drug's packaging.
     *
     * <p>This field contains additional notes about the drug's packaging.</p>
     */
    @JsonProperty("pack_note")
    String packNote;

    /**
     * Additional notes about the drug's packaging in Ukrainian.
     *
     * <p>This field contains additional notes about the drug's packaging in Ukrainian, if available.</p>
     *
     * @default ""
     */
    @JsonProperty("pack_note_ua")
    String packNoteUa = "";

    /**
     * Additional notes about the drug.
     *
     * <p>This field contains any additional notes about the drug.</p>
     */
    String note;
    /**
     * Additional notes about the drug in Ukrainian.
     *
     * <p>This field contains additional notes about the drug in Ukrainian, if available.</p>
     */

    @JsonProperty("note_ua")
    String noteUa;

    /**
     * Number associated with the drug.
     *
     * <p>This field represents a number associated with the drug, such as batch number.</p>
     *
     * @default ""
     */
    String numb = "";

    /**
     * Manufacturer of the drug.
     *
     * <p>This field specifies the manufacturer of the drug.</p>
     */
    String make;

    /**
     * Manufacturer of the drug in Ukrainian.
     *
     * <p>This field specifies the manufacturer of the drug in Ukrainian, if available.</p>
     */
    @JsonProperty("make_ua")
    String makeUa = "";

    /**
     * Concatenates drug information into a formatted string.
     *
     * <p>This method returns a formatted string containing the drug's name, dosage,
     * form, packaging, and additional notes in Ukrainian.</p>
     *
     * @return A formatted string with drug information.
     */
    public String getDrugData() {
        String info = this.nameUa + " " + this.doseUa + " " + this.formUa + " " + this.packUa;
        info += " " + this.packNoteUa + ", №" + this.numb + ", " + this.makeUa;

        return info;
    }
}