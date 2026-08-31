using Microsoft.EntityFrameworkCore;
using RoQuiApi.RoQui.Head.Model;

namespace RoQuiApi.Data
{
    public class AppDbContext : DbContext
    {
        public AppDbContext(DbContextOptions<AppDbContext> opt) : base(opt)
        {

        }

        public DbSet<Taxpayer> Taxpayers { get; set; }


        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {

        }
    }
}