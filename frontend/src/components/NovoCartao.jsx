import { useState } from 'react'

export default function NovoCartao({ onCriar }) {
  const [titulo, setTitulo] = useState('')
  const [tipo, setTipo] = useState('TAREFA')
  const [descricao, setDescricao] = useState('')
  const [detalhe, setDetalhe] = useState('')

  const rotulos = {
    TAREFA: 'Critérios de aceite',
    BUG: 'Passos para reprodução',
    MELHORIA: 'Justificativa da melhoria',
  }

  function enviar(e) {
    e.preventDefault()
    if (!titulo.trim() || !detalhe.trim()) return
    onCriar({ tipo, titulo: titulo.trim(), descricao: descricao.trim(), detalhe: detalhe.trim() })
    setTitulo('')
    setDescricao('')
    setDetalhe('')
  }

  return (
    <form className="novo" onSubmit={enviar}>
      <input value={titulo} onChange={(e) => setTitulo(e.target.value)} placeholder="Novo cartão" required />
      <select value={tipo} onChange={(e) => { setTipo(e.target.value); setDetalhe('') }}>
        <option value="TAREFA">Tarefa</option>
        <option value="BUG">Bug</option>
        <option value="MELHORIA">Melhoria</option>
      </select>
      <input value={descricao} onChange={(e) => setDescricao(e.target.value)} placeholder="Descrição (opcional)" />
      <input value={detalhe} onChange={(e) => setDetalhe(e.target.value)} placeholder={rotulos[tipo]}
        aria-label={rotulos[tipo]} required />
      <button type="submit">Adicionar</button>
    </form>
  )
}
