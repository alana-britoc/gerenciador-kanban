export const SEM_PRIORIDADE = 'SEM'

export const FILTROS_VAZIOS = { texto: '', tipos: [], prioridades: [], etiquetas: [] }

export function normalizar(texto) {
  return (texto ?? '').normalize('NFD').replace(/\p{Diacritic}/gu, '').toLowerCase().trim()
}

export function temFiltro(filtros) {
  return Boolean(normalizar(filtros.texto)) || filtros.tipos.length > 0 || filtros.prioridades.length > 0 || filtros.etiquetas.length > 0
}

export function etiquetasDe(cartoes) {
  const todas = new Set(cartoes.flatMap((c) => c.extras?.etiquetas ?? []))
  return [...todas].sort((a, b) => a.localeCompare(b, 'pt-BR'))
}

export function aplicarFiltros(cartoes, filtros) {
  const busca = normalizar(filtros.texto)
  return cartoes.filter((c) => {
    if (busca && !normalizar(`${c.titulo} ${c.descricao ?? ''}`).includes(busca)) return false
    if (filtros.tipos.length > 0 && !filtros.tipos.includes(c.tipo)) return false
    const prioridade = c.extras?.prioridade ?? SEM_PRIORIDADE
    if (filtros.prioridades.length > 0 && !filtros.prioridades.includes(prioridade)) return false
    const etiquetas = c.extras?.etiquetas ?? []
    if (filtros.etiquetas.length > 0 && !filtros.etiquetas.some((e) => etiquetas.includes(e))) return false
    return true
  })
}
