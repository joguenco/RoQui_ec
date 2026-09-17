using System.ComponentModel.DataAnnotations.Schema;

namespace RoQuiApi.RoQui.Invoice.Model;

[Table("withholds_document_taxes")]
public class WithholdDocumentTax
{
    [Column("id")]
    public int Id { get; set; }

    [Column("tax_code", TypeName = "varchar")]
    public required string TaxCode { get; set; }

    [Column("percentage_code", TypeName = "varchar")]
    public required string PercentageCode { get; set; }

    [Column("tax_base", TypeName = "decimal")]
    public required decimal TaxBase { get; set; }

    [Column("tax_iva", TypeName = "decimal")]
    public required decimal TaxIva { get; set; }

    [Column("value", TypeName = "decimal")]
    public required decimal Value { get; set; }

    [Column("withhold_support_id")]
    public int WithholdSupportId { get; set; }

    public required WithholdSupport WithholdSupport { get; set; }
}
