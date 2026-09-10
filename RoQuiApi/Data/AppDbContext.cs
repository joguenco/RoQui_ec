namespace RoQuiApi.Data;

using Microsoft.EntityFrameworkCore;
using RoQuiApi.RoQui.Electronic.Model;
using RoQuiApi.RoQui.Head.Model;
using RoQuiApi.RoQui.Invoice.Model;



public class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> opt) : base(opt)
    {

    }

    public DbSet<Taxpayer> Taxpayers { get; set; }
    public DbSet<Establishment> Establishments { get; set; }
    public DbSet<Document> Documents { get; set; }
    public DbSet<DocumentDetail> DocumentDetails { get; set; }
    public DbSet<DocumentDetailTax> DocumentDetailTaxes { get; set; }
    public DbSet<DocumentPayment> Payments { get; set; }
    public DbSet<Electronic> Electronics { get; set; }
    public DbSet<Parameter> Parameters { get; set; }
    public DbSet<Withhold> Withholds { get; set; }
    public DbSet<WithholdSupport> WithholdSupports { get; set; }
    public DbSet<WithholdDetail> WithholdDetails { get; set; }
    public DbSet<WithholdDocumentTax> WithholdDocumentTaxes { get; set; }


    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        //ForeignKey constraints
        modelBuilder.Entity<Taxpayer>()
            .HasMany(t => t.Establishments)
            .WithOne(e => e.Taxpayer)
            .HasForeignKey(e => e.TaxpayerId)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<Document>()
            .HasMany(i => i.DocumentDetails)
            .WithOne(d => d.Document)
            .HasForeignKey(d => d.DocumentId)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<DocumentDetail>()
            .HasMany(d => d.InvoiceDetailTaxes)
            .WithOne(t => t.DocumentDetail)
            .HasForeignKey(t => t.DocumentDetailId)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<Document>()
            .HasMany(d => d.Payments)
            .WithOne(p => p.Document)
            .HasForeignKey(p => p.DocumentId)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<Withhold>()
            .HasMany(w => w.WithholdSupports)
            .WithOne(s => s.Withhold)
            .HasForeignKey(s => s.WithholdId)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<WithholdSupport>()
            .HasMany(s => s.WithholdDetails)
            .WithOne(d => d.WithholdSupport)
            .HasForeignKey(d => d.WithholdSupportId)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<WithholdSupport>()
            .HasMany(s => s.WithholdDocumentTaxes)
            .WithOne(t => t.WithholdSupport)
            .HasForeignKey(t => t.WithholdSupportId)
            .OnDelete(DeleteBehavior.Cascade);

        //Unique constraints
        modelBuilder.Entity<Document>()
            .HasIndex(i => new { i.Code, i.Number })
            .IsUnique();

        modelBuilder.Entity<Document>()
            .HasIndex(i => i.AccessKey)
            .IsUnique();

        modelBuilder.Entity<Withhold>()
            .HasIndex(w => new { w.Code, w.Number })
            .IsUnique();

        modelBuilder.Entity<Withhold>()
            .HasIndex(w => w.AccessKey)
            .IsUnique();
    }
}
