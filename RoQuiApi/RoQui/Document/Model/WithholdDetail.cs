using System.ComponentModel.DataAnnotations.Schema;

namespace RoQuiApi.RoQui.Invoice.Model;

[Table("withholds_detail")]
public class WithholdDetail
{
    [Column("id")]
    public int Id { get; set; }

    [Column("line", TypeName = "int")]
    public int Line { get; set; }

    [Column("tax_code", TypeName = "varchar")]
    public required string TaxCode { get; set; }

    [Column("withhold_code", TypeName = "varchar")]
    public required string WithholdCode { get; set; }

    [Column("base_value", TypeName = "decimal")]
    public required decimal BaseValue { get; set; }

    [Column("percentage", TypeName = "decimal")]
    public required decimal Percentage { get; set; }

    [Column("withholded_value", TypeName = "decimal")]
    public required decimal WithholdedValue { get; set; }

    [Column("withhold_support_id")]
    public int WithholdSupportId { get; set; }

    public required WithholdSupport WithholdSupport { get; set; }
}
