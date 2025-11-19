import { useState, useEffect } from 'react';
import { FormControl, InputLabel, Select, MenuItem, FormHelperText } from '@mui/material';
import { categoryService } from '../services';

/**
 * CategorySelect Component
 * Dropdown component for selecting expense categories
 */
const CategorySelect = ({ value, onChange, error, helperText, required = true, disabled = false }) => {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchCategories();
  }, []);

  /**
   * Fetch all available categories
   */
  const fetchCategories = async () => {
    try {
      const data = await categoryService.getAllCategories();
      setCategories(data);
    } catch (err) {
      console.error('Failed to fetch categories:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <FormControl fullWidth error={error} disabled={disabled || loading}>
      <InputLabel required={required}>Category</InputLabel>
      <Select
        value={value || ''}
        label="Category"
        onChange={(e) => onChange(e.target.value)}
      >
        <MenuItem value="">
          <em>{loading ? 'Loading...' : 'Select a category'}</em>
        </MenuItem>
        {categories.map((category) => (
          <MenuItem key={category.id} value={category.id}>
            {category.name}
          </MenuItem>
        ))}
      </Select>
      {helperText && <FormHelperText>{helperText}</FormHelperText>}
    </FormControl>
  );
};

export default CategorySelect;
