import { useEffect, useState } from 'react'
import { motion } from 'framer-motion'
import { ShoppingBag, Search, Palette, Plus, Tag } from 'lucide-react'
import { productService, TShirtProduct } from '@/services/productService'
import toast from 'react-hot-toast'

export default function ProductsPage() {
  const [products, setProducts] = useState<TShirtProduct[]>([])
  const [loading, setLoading] = useState(true)
  const [search, setSearch] = useState('')
  const [selectedCategory, setSelectedCategory] = useState('ALL')

  useEffect(() => {
    const load = async () => {
      setLoading(true)
      try {
        const data = await productService.getProducts()
        setProducts(data)
      } catch (err: any) {
        toast.error(err?.response?.data?.message || 'Failed to load products')
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [])

  const categories = ['ALL', ...new Set(products.map(p => p.category).filter(Boolean))]

  const filtered = products.filter(p => {
    const matchSearch = !search || p.name?.toLowerCase().includes(search.toLowerCase()) || p.code?.toLowerCase().includes(search.toLowerCase())
    const matchCategory = selectedCategory === 'ALL' || p.category === selectedCategory
    return matchSearch && matchCategory
  })

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="page-title">Product Catalog</h1>
        <div className="flex items-center gap-2 px-3 py-1.5 bg-primary-50 dark:bg-primary-900/20 rounded-lg">
          <ShoppingBag size={14} className="text-primary-600" />
          <span className="text-xs font-semibold text-primary-600">{products.length} Products</span>
        </div>
      </div>

      {/* Filters */}
      <div className="flex flex-col sm:flex-row gap-3">
        <div className="relative flex-1">
          <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
          <input type="text" placeholder="Search products..." value={search} onChange={(e) => setSearch(e.target.value)}
            className="w-full pl-10 pr-4 py-2.5 bg-white dark:bg-gray-900 border border-gray-200 dark:border-gray-700 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary-500" />
        </div>
        <select value={selectedCategory} onChange={(e) => setSelectedCategory(e.target.value)}
          className="px-4 py-2.5 bg-white dark:bg-gray-900 border border-gray-200 dark:border-gray-700 rounded-xl text-sm focus:outline-none">
          {categories.map(c => <option key={c} value={c}>{c === 'ALL' ? 'All Categories' : c}</option>)}
        </select>
      </div>

      {/* Products Table */}
      <div className="bg-white dark:bg-gray-900 rounded-2xl border border-gray-100 dark:border-gray-800 overflow-hidden">
        {loading ? (
          <div className="p-12 text-center animate-pulse">
            <div className="h-6 w-40 bg-gray-200 dark:bg-gray-700 rounded mx-auto" />
          </div>
        ) : filtered.length === 0 ? (
          <div className="p-12 text-center text-gray-400">
            <Tag size={32} className="mx-auto mb-3 opacity-50" />
            <p className="text-sm">No products found</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-gray-50 dark:border-gray-800">
                  <th className="px-5 py-3 text-left table-header">Product</th>
                  <th className="px-5 py-3 text-left table-header">Category</th>
                  <th className="px-5 py-3 text-left table-header">Fabric</th>
                  <th className="px-5 py-3 text-right table-header">Price</th>
                  <th className="px-5 py-3 text-left table-header">Colors</th>
                  <th className="px-5 py-3 text-left table-header">Sizes</th>
                  <th className="px-5 py-3 text-left table-header">Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-50 dark:divide-gray-800">
                {filtered.map((p) => (
                  <tr key={p.id} className="hover:bg-gray-50/50 dark:hover:bg-gray-800/30 transition-colors">
                    <td className="px-5 py-3.5">
                      <p className="text-sm font-semibold text-gray-900 dark:text-white">{p.name}</p>
                      <p className="text-xs text-gray-400 font-mono">{p.code}</p>
                    </td>
                    <td className="px-5 py-3.5">
                      <span className="text-sm text-gray-600 dark:text-gray-400">{p.category}</span>
                    </td>
                    <td className="px-5 py-3.5">
                      <span className="text-sm text-gray-600 dark:text-gray-400">{p.fabric} ({p.gsm} GSM)</span>
                    </td>
                    <td className="px-5 py-3.5 text-right">
                      <span className="text-sm font-bold text-gray-900 dark:text-white">₹{p.basePrice?.toFixed(2)}</span>
                    </td>
                    <td className="px-5 py-3.5">
                      <div className="flex items-center gap-1">
                        {p.availableColors?.slice(0, 5).map((c, i) => (
                          <div key={i} className="w-4 h-4 rounded-full border border-gray-200" style={{ backgroundColor: c.hex }} title={c.name} />
                        ))}
                        {(p.availableColors?.length || 0) > 5 && <span className="text-xs text-gray-400">+{p.availableColors.length - 5}</span>}
                      </div>
                    </td>
                    <td className="px-5 py-3.5">
                      <span className="text-xs text-gray-500">{p.availableSizes?.join(', ')}</span>
                    </td>
                    <td className="px-5 py-3.5">
                      <span className={`px-2 py-0.5 rounded-lg text-xs font-semibold ${p.active ? 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400' : 'bg-gray-100 text-gray-500'}`}>
                        {p.active ? 'Active' : 'Inactive'}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}
