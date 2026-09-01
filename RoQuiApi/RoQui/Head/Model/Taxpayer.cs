namespace RoQuiApi.RoQui.Head.Model;

using System.ComponentModel.DataAnnotations.Schema;

[Table("taxpayers")]
public class Taxpayer
{
    [Column("id")]
    public int Id { get; set; }

    [Column("identification", TypeName = "varchar")]
    public required string Identification { get; set; }

    [Column("legal_name", TypeName = "varchar")]
    public required string LegalName { get; set; }

    [Column("forced_accounting", TypeName = "varchar")]
    public required string ForcedAccounting { get; set; }

    [Column("special_taxpayer", TypeName = "varchar")]
    public string? SpecialTaxpayer { get; set; }

    [Column("retention_agent", TypeName = "varchar")]
    public string? RetentionAgent { get; set; }

    [Column("rimpe", TypeName = "varchar")]
    public string? Rimpe { get; set; }

    public virtual required ICollection<Establishmet> Establishments { get; set; }
}