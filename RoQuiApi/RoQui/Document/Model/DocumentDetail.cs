using System.ComponentModel.DataAnnotations.Schema;

namespace RoQuiApi.RoQui.Invoice.Model;

[Table("documents_detail")]
public class DocumentDetail
{
    [Column("id")]
    public int Id { get; set; }

    [Column("line", TypeName = "int")]
    public int Line { get; set; }

    [Column("product_code", TypeName = "varchar")]
    public required string ProductCode { get; set; }

    [Column("product_name", TypeName = "varchar")]
    public required string ProductName { get; set; }

    [Column("quantity", TypeName = "decimal")]
    public required decimal Quantity { get; set; }

    [Column("unit", TypeName = "varchar")]
    public string? Unit { get; set; }

    [Column("unit_price", TypeName = "decimal")]
    public required decimal UnitPrice { get; set; }

    [Column("tax_code", TypeName = "varchar")]
    public required string TaxCode { get; set; }

    [Column("tax_iva", TypeName = "decimal")]
    public required decimal TaxIva { get; set; }

    [Column("value_iva", TypeName = "decimal")]
    public required decimal ValueIva { get; set; }

    [Column("discount", TypeName = "decimal")]
    public required decimal Discount { get; set; }

    [Column("total_without_tax", TypeName = "decimal")]
    public required decimal TotalWithoutTax { get; set; }

    [Column("total", TypeName = "decimal")]
    public required decimal Total { get; set; }

    [Column("document_id")]
    public int DocumentId { get; set; }

    public required Document Document { get; set; }

    public virtual required ICollection<DocumentDetailTax> InvoiceDetailTaxes { get; set; }
}