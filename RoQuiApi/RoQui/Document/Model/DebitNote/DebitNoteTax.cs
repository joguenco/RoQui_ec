using System.ComponentModel.DataAnnotations.Schema;

namespace RoQuiApi.RoQui.Invoice.Model;

// Los impuestos de la nota de debito van en la cabecera, no en cada linea.
// Por eso cuelgan del documento y no de documents_detail como en la factura.
[Table("debit_notes_tax")]
public class DebitNoteTax
{
    [Column("id")]
    public int Id { get; set; }

    [Column("tax_code", TypeName = "varchar")]
    public required string TaxCode { get; set; }

    [Column("tax_code_percentage", TypeName = "varchar")]
    public required string TaxCodePercentage { get; set; }

    [Column("tax_value", TypeName = "decimal")]
    public required decimal TaxValue { get; set; }

    [Column("base", TypeName = "decimal")]
    public required decimal Base { get; set; }

    [Column("value", TypeName = "decimal")]
    public required decimal Value { get; set; }

    [Column("document_id")]
    public int DocumentId { get; set; }
}
