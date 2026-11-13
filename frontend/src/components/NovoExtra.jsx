import { useState } from 'react'

export default function NovoExtra({ onAdicionar, onCancelar }) {
  const [tipo, setTipo] = useState('PRIORIDADE')
  const [valor, setValor] = useState('ALTA')

  function trocarTipo(novoTipo) {
    setTipo(novoTipo)
    setValor(novoTipo === 'PRIORIDADE' ? 'ALTA' : '')
  }

  async function enviar(e) {
    e.preventDefault()
    if (!valor.trim()) return
    const ok = await onAdicionar(tipo, valor.trim())
    if (ok) onCancelar()
  }

  return (
    <form className="novo-extra" onSubmit={enviar}>
      <select value={tipo} onChange={(e) => trocarTipo(e.target.value)} aria-label="Tipo de extra">
        <option value="PRIORIDADE">Prioridade</option>
        <option value="PRAZO">Prazo</option>
        <option value="ETIQUETA">Etiqueta</option>
      </select>
      {tipo === 'PRIORIDADE' && (
        <select value={valor} onChange={(e) => setValor(e.target.value)} aria-label="Prioridade">
          <option value="BAIXA">Baixa</option>
          <option value="MEDIA">Média</option>
          <option value="ALTA">Alta</option>
        </select>
      )}
      {tipo === 'PRAZO' && (
        <input type="date" value={valor} onChange={(e) => setValor(e.target.value)} aria-label="Prazo" required />
      )}
      {tipo === 'ETIQUETA' && (
        <input value={valor} onChange={(e) => setValor(e.target.value)} placeholder="Etiqueta"
          aria-label="Etiqueta" maxLength={30} required autoFocus />
      )}
      <div className="novo-extra-acoes">
        <button type="button" onClick={onCancelar}>cancelar</button>
        <button type="submit">adicionar</button>
      </div>
    </form>
  )
}
