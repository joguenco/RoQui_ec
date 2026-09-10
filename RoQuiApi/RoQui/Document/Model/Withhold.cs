using System.ComponentModel.DataAnnotations.Schema;

namespace RoQuiApi.RoQui.Invoice.Model;

[Table("withholds")]
public class Withhold
{
    [Column("id")]
    public int Id { get; set; }

    [Column("code", TypeName = "varchar")]
    public required string Code { get; set; }

    [Column("number", TypeName = "varchar")]
    public required string Number { get; set; }

    [Column("date", TypeName = "date")]
    public required DateTime Date { get; set; }

    [Column("identification_type", TypeName = "varchar")]
    public required string IdentificationType { get; set; }

    [Column("identification", TypeName = "varchar")]
    public required string Identification { get; set; }

    [Column("legal_name", TypeName = "varchar")]
    public required string LegalName { get; set; }

    [Column("access_key", TypeName = "varchar")]
    public required string AccessKey { get; set; }

    [Column("fiscal_period", TypeName = "varchar")]
    public required string FiscalPeriod { get; set; }

    [Column("related", TypeName = "varchar")]
    public string? Related { get; set; }

    [Column("created_at", TypeName = "timestamp without time zone")]
    public DateTime CreatedAt { get; set; }

    public virtual required ICollection<WithholdSupport> WithholdSupports { get; set; }
}
