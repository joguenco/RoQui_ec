using System.ComponentModel.DataAnnotations.Schema;

namespace RoQuiApi.RoQui.Invoice.Model;

[Table("invoice_details_taxes")]
public class InvoiceDetailTax
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

    [Column("invoice_detail_id")]
    public int InvoiceDetailId { get; set; }

    public required InvoiceDetail InvoiceDetail { get; set; }
}