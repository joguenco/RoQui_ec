using System.ComponentModel.DataAnnotations.Schema;

namespace RoQuiApi.RoQui.Invoice.Model;

[Table("withholds_support")]
public class WithholdSupport
{
    [Column("id")]
    public int Id { get; set; }

    [Column("code_support", TypeName = "varchar")]
    public required string CodeSupport { get; set; }

    [Column("code_document_support", TypeName = "varchar")]
    public required string CodeDocumentSupport { get; set; }

    [Column("number_document_support", TypeName = "varchar")]
    public required string NumberDocumentSupport { get; set; }

    [Column("date_document_support", TypeName = "date")]
    public required DateTime DateDocumentSupport { get; set; }

    [Column("authorization_document_support", TypeName = "varchar")]
    public required string AuthorizationDocumentSupport { get; set; }

    [Column("total_without_taxes", TypeName = "decimal")]
    public required decimal TotalWithoutTaxes { get; set; }

    [Column("total", TypeName = "decimal")]
    public required decimal Total { get; set; }

    [Column("withhold_id")]
    public int WithholdId { get; set; }

    public required Withhold Withhold { get; set; }

    public virtual required ICollection<WithholdDetail> WithholdDetails { get; set; }

    public virtual required ICollection<WithholdDocumentTax> WithholdDocumentTaxes { get; set; }
}
