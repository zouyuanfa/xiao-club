import { useNavigate } from 'react-router-dom'
import { useState } from 'react'
import axios from 'axios'
import { fields } from '../config/fields'
import headerImg from '../assets/1-C3RSZf3V.png'
import submitImg from '../assets/submit-H243owGr.png'

const SubmitPage = () => {
  const navigate = useNavigate()
  const [formData, setFormData] = useState(() => {
    const init = {}
    fields.forEach((field) => {
      if (field.type === 'checkbox') {
        init[field.model] = []
      }
    })
    return init
  })
  const [errors, setErrors] = useState({})

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
    setErrors((prev) => ({ ...prev, [name]: false }))
  }

  const handleCheckboxChange = (model, value) => {
    setFormData((prev) => {
      const list = Array.isArray(prev[model]) ? prev[model] : []
      const next = list.includes(value)
        ? list.filter((item) => item !== value)
        : [...list, value]
      return { ...prev, [model]: next }
    })
    if (errors[model]) {
      setErrors((prev) => ({ ...prev, [model]: false }))
    }
  }

  const validateField = (model, value, required, type) => {
    if (type === 'checkbox') {
      if (required && (!Array.isArray(value) || value.length === 0)) {
        return '请至少选择一个选项'
      }
      return false
    }

    if (required && (!value || (typeof value === 'string' && value.trim() === ''))) {
      return '此项为必填项'
    }

    if (model === 'phone' && value) {
      // 手机号使用前3+后4模式，这里验证合并后的结果
      const phonePrefix = formData.phonePrefix || ''
      const phoneSuffix = formData.phoneSuffix || ''
      if (required && (!phonePrefix || !phoneSuffix)) {
        return '请输入完整的手机号前三位和后四位'
      }
    }

    return false
  }

  const validateAll = () => {
    const nextErrors = {}
    const messages = []

    fields.forEach((field) => {
      const value = field.model === 'phone'
        ? ((formData.phonePrefix || '') || (formData.phoneSuffix || ''))
          ? `${formData.phonePrefix || ''}****${formData.phoneSuffix || ''}`
          : ''
        : formData[field.model]

      const message = validateField(field.model, value, field.required, field.type)
      nextErrors[field.model] = message
      if (message) {
        messages.push(message)
      }
    })

    setErrors(nextErrors)
    return messages.length === 0
  }

  const handleBlur = (field) => {
    const value = field.model === 'phone'
      ? ((formData.phonePrefix || '') || (formData.phoneSuffix || ''))
        ? `${formData.phonePrefix || ''}****${formData.phoneSuffix || ''}`
        : ''
      : formData[field.model]
    const message = validateField(field.model, value, field.required, field.type)
    if (message) {
      setErrors((prev) => ({ ...prev, [field.model]: message }))
    }
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    if (validateAll()) {
      console.log('Form submitted:', formData)
      submit()
    }
  }

  const submit = () => {
    const payload = {
      ...formData,
      phone:
        (formData.phonePrefix || '') && (formData.phoneSuffix || '')
          ? `${formData.phonePrefix}****${formData.phoneSuffix}`
          : formData.phone || '',
    }
    delete payload.phonePrefix
    delete payload.phoneSuffix

    axios
      .post('/api/survey/add', payload, {
        headers: { 'Content-Type': 'application/json' },
      })
      .then((resp) => {
        console.log(resp)
        if (resp.status === 200 && resp.data.success) {
          setFormData({})
          navigate(`/sucess?number=${resp.data.data}&name=${formData.name}`)
        } else {
          onError(resp)
        }
      })
      .catch(onError)
  }

  const onError = (error) => {
    alert('提交失败')
    console.error(error)
  }

  const renderField = (field) => {
    switch (field.type) {
      case 'select':
        return (
          <select
            name={field.model}
            value={formData[field.model] || ''}
            onChange={handleChange}
            onBlur={() => handleBlur(field)}
            className={`form-select ${errors[field.model] ? 'form-input-error' : ''}`}
          >
            <option value="">请选择</option>
            {field.options.map((opt, idx) => (
              <option key={idx} value={opt}>
                {opt}
              </option>
            ))}
          </select>
        )

      case 'radio':
        return (
          <div className="checkbox-group">
            {field.options.map((opt, idx) => (
              <label key={idx} className="checkbox-label">
                <input
                  type="radio"
                  name={field.model}
                  value={opt.value}
                  checked={formData[field.model] === opt.value}
                  onChange={handleChange}
                  className="checkbox-input"
                />
                <span className="checkbox-custom" />
                {opt.label}
              </label>
            ))}
          </div>
        )

      case 'checkbox':
        return (
          <div
            className={`checkbox-group ${
              errors[field.model] ? 'checkbox-group-error' : ''
            }`}
          >
            {field.options.map((opt, idx) => {
              if (opt.type === 'group') {
                return (
                  <div key={idx} className="checkbox-section-title">
                    {opt.label}
                  </div>
                )
              }
              return (
                <label key={idx} className="checkbox-label">
                  <input
                    type="checkbox"
                    checked={
                      Array.isArray(formData[field.model]) &&
                      formData[field.model].includes(opt.value)
                    }
                    onChange={() => handleCheckboxChange(field.model, opt.value)}
                    className="checkbox-input"
                  />
                  <span className="checkbox-custom" />
                  {opt.label}
                </label>
              )
            })}
          </div>
        )

      case 'textarea':
        return (
          <textarea
            name={field.model}
            value={formData[field.model] || ''}
            onChange={handleChange}
            onBlur={() => handleBlur(field)}
            className={`form-input ${errors[field.model] ? 'form-input-error' : ''}`}
            placeholder={field.placeholder}
          />
        )

      default:
        // input 类型，phone 字段特殊处理
        if (field.model === 'phone') {
          return (
            <div className="phone-mask-group">
              <input
                type="text"
                inputMode="numeric"
                maxLength={3}
                value={formData.phonePrefix || ''}
                onChange={(e) => {
                  const val = e.target.value.replace(/\D/g, '').slice(0, 3)
                  const suffix = formData.phoneSuffix || ''
                  setFormData((prev) => ({
                    ...prev,
                    phonePrefix: val,
                    phone: val || suffix ? `${val}****${suffix}` : '',
                  }))
                  setErrors((prev) => ({ ...prev, [field.model]: false }))
                }}
                onBlur={() => handleBlur(field)}
                className={`form-input phone-segment ${
                  errors[field.model] ? 'form-input-error' : ''
                }`}
                placeholder="前三"
              />
              <span className="phone-mask-separator">****</span>
              <input
                type="text"
                inputMode="numeric"
                maxLength={4}
                value={formData.phoneSuffix || ''}
                onChange={(e) => {
                  const val = e.target.value.replace(/\D/g, '').slice(0, 4)
                  const prefix = formData.phonePrefix || ''
                  setFormData((prev) => ({
                    ...prev,
                    phoneSuffix: val,
                    phone: prefix || val ? `${prefix}****${val}` : '',
                  }))
                  setErrors((prev) => ({ ...prev, [field.model]: false }))
                }}
                onBlur={() => handleBlur(field)}
                className={`form-input phone-segment ${
                  errors[field.model] ? 'form-input-error' : ''
                }`}
                placeholder="后四"
              />
            </div>
          )
        }

        return (
          <input
            type={field.type}
            name={field.model}
            value={formData[field.model] || ''}
            onChange={handleChange}
            onBlur={() => handleBlur(field)}
            className={`form-input ${errors[field.model] ? 'form-input-error' : ''}`}
            placeholder={field.placeholder}
          />
        )
    }
  }

  return (
    <div className="imgcommon" style={{ overflow: 'auto' }}>
      <img src={headerImg} className="img img1" alt="" />
      <div className="form-container">
        <form onSubmit={handleSubmit} className="form">
          {fields.map((field, idx) => (
            <div key={idx} className="form-group">
              <label className="form-label">
                <span>
                  {field.label}
                  {field.required && <span className="required"> *</span>}
                </span>
                {renderField(field)}
                {errors[field.model] && (
                  <span className="form-error">
                    {typeof errors[field.model] === 'string'
                      ? errors[field.model]
                      : '此项为必填项'}
                  </span>
                )}
              </label>
            </div>
          ))}
          <img
            src={submitImg}
            className="submit"
            onClick={handleSubmit}
            alt="提交"
          />
        </form>
      </div>
    </div>
  )
}

export default SubmitPage
