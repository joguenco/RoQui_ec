namespace RoQuiApi.Data;

using Microsoft.EntityFrameworkCore;
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

        //Unique constraints
        modelBuilder.Entity<Document>()
            .HasIndex(i => new { i.Code, i.Number })
            .IsUnique();

        modelBuilder.Entity<Document>()
            .HasIndex(i => i.AccessKey)
            .IsUnique();
    }
}
