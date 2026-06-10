import apiClient from './apiClient'

export interface ProductImage {
  id: string
  imageUrl: string
  cloudinaryPublicId?: string
  isPrimary: boolean
  sortOrder?: number
  colorHex?: string
}

export interface TShirtProduct {
  id: string
  productCode: string
  name: string
  description?: string
  brand: string
  category: string
  fabric: string
  gsm: string
  neckType: string
  sleeveType: string
  minimumOrderQuantity: number
  basePrice: number
  discountPrice?: number | null
  effectivePrice: number
  active: boolean
  stockQuantity: number
  availableSizes: string[]
  availableColors: string[]
  printTypes: string[]
  images: ProductImage[]
}

export const productService = {
  getProducts: async (): Promise<TShirtProduct[]> => {
    const res = await apiClient.get('/v1/products')
    return res.data?.data ?? res.data ?? []
  },
}

